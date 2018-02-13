import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.util.LinkedList;
import java.util.List;

/**
 * New version of CADO Scrapping using simpler objects with hashmaps
 *
 * This class builds up company business objects from the CADO database. The CADO Database returns company information
 * (which is captured in the Has based Company object - CADOCompany)
 *
 * This class is completely dependent on the website code.
 * This class is designed to be the only change necessary if style or data display on the CADO site changes.
 * (If there is a data structure change the whole package will have to be updated.)
 *
 * Created by josh.taylor on 1/23/2018.
 */
public class CADOScraper {

    public static final String SEARCH_PAGE_URL = "https://cado.eservices.gov.nl.ca/CadoInternet/Company/CompanyNameNumberSearch.aspx";
    public static final String COMPANY_DETAILS_PAGE_URL = "https://cado.eservices.gov.nl.ca/CadoInternet/Company/CompanyDetails.aspx";

    /**
     * Return a list of companies that are returned from a two keyword query on the CADO website.
     * This method will retrieve all of the company details - therefore, there will be a webclick / HTTP request
     * for each company returned in the query.
     *
     * If only a single keyword search is performed pass in a blank or null String.)
     *
     * @param nameKeyword1 First keyword
     * @param nameKeyword2 Second keyword (optional)
     * @return
     */
    public static List<CADOCompany> getCompanyList(String nameKeyword1, String nameKeyword2) {
        //TODO handle exceptions
        return processSearchResults(nameKeyword1, nameKeyword2, "");
    }

    /**
     * Get a list of Companies objects from a business number. Usually the list will only contain the most current version
     * of the company object for the business number. However, in some cases, previous versions of the companies are also listed,
     * notably earlier versions of the company with different names
     *
     * @param businessNumber Corporation Number used to index CADO database
     * @return Companies with that business number.
     */
    public static List<CADOCompany> getCompanyList(String businessNumber) {
        //TODO handle exceptions
        return processSearchResults(null, null, businessNumber);
    }

    /**
     * Return company objects based on a CADO query. This method calls the getSearchResults page method to get the
     * HTML page returned and then checks to see if the page navigated to is a company details (ie. the search returned
     * a single company) or if a list of companies is appended to the current search page.
     *
     * The method will also deal with exceptions including: no results found and too many results found to display.
     *
     * @param nameKeyword1 Input into first keyword text field on CADO site
     * @param nameKeyword2 Input into second keyword text field space 2 on CADO site
     * @param businessNumber Input into Business Number text field on CADO site
     * @return
     */
    private static List<CADOCompany> processSearchResults(String nameKeyword1, String nameKeyword2, String businessNumber) {

        //Retrieve the search results page
        HtmlPage resultsPage = getSearchResultsPage(nameKeyword1, nameKeyword2, businessNumber);

        if (resultsPage.getBaseURL().toString().equals(SEARCH_PAGE_URL)) {
            return processCompaniesFromSearchPage(resultsPage);

        } else if (resultsPage.getBaseURL().toString().equals(COMPANY_DETAILS_PAGE_URL)) {
            //There is only a single company shown on a detailed page. Build a list with just one company
            //TODO Josh has to port this over
            LinkedList<CADOCompany> companyList = new LinkedList<>();
            //companyList.addFirst(processCompanyDetail(resultsPage).getCompany());
            return companyList;

        } else {
            System.out.println("\n**UH OH - URL comparison failed");
            //TODO Deal with these
        }
        return null;
    }

    /**
     * This Method populates the search form, executes and returns the result page
     *
     * Execute a search on the CADO main page and return the resulting response. Page may vary between a company list
     * or a detail company view. This method contains all the information about the structure of the search form.
     *
     * @return HtmlPage containing search results
     */
    private static HtmlPage getSearchResultsPage(String nameKeyword1, String nameKeyword2, String businessNumber) {

        //TODO handle exception
        HtmlPage resultsPage = null;

        try {
            WebClient webClient = new WebClient();
            //TODO handle user management
            webClient.getCookieManager().clearCookies();

            HtmlPage page = webClient.getPage(SEARCH_PAGE_URL);

            //TODO Watch out for this
            //https://cado.eservices.gov.nl.ca/CADOInternet/ErrorPage.aspx?aspxerrorpath=/CADOInternet/CompanyDetail/CompanyNameNumberSearch.aspx
            //System.out.println("First HTML Grab>\n\n" + page.asXml());
            //TODO Use AssertEqualts
            //assertEquals("HtmlUnit - Welcome to HtmlUnit", startPage.getTitleText());


            //Grab the form
            HtmlForm form = page.getForms().get(0);     //Grab the form. It's the only form.

            //Populate the form
            if (nameKeyword1 != null && !nameKeyword1.isEmpty()) {
                form.getInputByName("txtNameKeywords1").setValueAttribute(nameKeyword1);
            }
            if (nameKeyword2 != null && !nameKeyword2.isEmpty()) {
                form.getInputByName("txtNameKeywords2").setValueAttribute(nameKeyword2);
            }
            if (businessNumber != null && !businessNumber.isEmpty()) {
                form.getInputByName("txtCompanyNumber").setValueAttribute(businessNumber); //TODO Check that it doesn't exceed 8 char
            }

            HtmlImageInput searchButton = (HtmlImageInput) form.getInputsByName("btnSearch").get(0);

            //TODO look into removing this casting.
            resultsPage = (HtmlPage) searchButton.click();

        }
        catch (ElementNotFoundException e){
            System.out.println("Element not found exception");
            System.err.println(e);

        } catch (Exception e) {         //if an HTTP/connection error occurs, handle JauntException.
            System.err.println(e);
        }

        return resultsPage;
    }

    /**
     * Build up a list of companies from a search page that has multiple company results. This involves clicking through
     * a link for each company which involves another HTTP request
     * @param resultsPage a results page from CADO search
     * @return List of company objects build from search results page
     */
    private static List<CADOCompany> processCompaniesFromSearchPage(HtmlPage resultsPage) {

        LinkedList<CADOCompany> companyList = new LinkedList<>();

        /*
                1) The multiple companies return in a table that's tagged with 'tableSearchResults'.
                    Grab that in resultsTable

                2) Within that table is a table within another table (seriously!) but it's nested inside a row.  Grab the row and then grab
                    a reference to the table within it.
                3) Within the inner table there are there are 5 rows (even though it's all one row) and each row has a single 'td' tag.
                    Note: td tags are used here because there are no span tags

                    These td tags contain the following information:
                    > CompanyDetail Name
                    > Status
                    > CompanyDetail Number
                    > Corporation Type
                    > Incorporation Date

                ++ This code all needs to update if the website changes. ++
         */
        try {
            HtmlTable resultsTable = (HtmlTable) resultsPage.getElementById("tableSearchResults");
            HtmlTableRow row = resultsTable.getRows().get(5);
            HtmlTable smallestResultsTable = (HtmlTable) (row.getElementsByTagName("table").get(0)).getElementsByTagName("table").get(0);
            DomNodeList<HtmlElement> tdElementList = smallestResultsTable.getElementsByTagName("td");

            for (int i = 0; i < tdElementList.size(); i++) {
                if (i % 5 == 0 && i >= 5 & i < tdElementList.size() - 4) {
                    CADOCompany company = new CADOCompany();

                    try {
                        company.setValue(CADOCompany.NAME, tdElementList.get(i).getChildNodes().get(0) != null ?
                                tdElementList.get(i).getChildNodes().get(0).asText() : "");
                        company.setValue(CADOCompany.NUMBER, tdElementList.get(i + 2).getChildNodes().get(0) != null ?
                                tdElementList.get(i + 2).getChildNodes().get(0).asText() : "");
                        company.setValue(CADOCompany.STATUS, tdElementList.get(i + 1).getChildNodes().get(0) != null ?
                                tdElementList.get(i + 1).getChildNodes().get(0).asText() : "");
                        company.setValue(CADOCompany.CORPORATION_TYPE, tdElementList.get(i + 3).getChildNodes().get(0) != null ?
                                tdElementList.get(i + 3).getChildNodes().get(0).asText() : "");
                        company.setValue(CADOCompany.INCORPORATION_DATE, tdElementList.get(i + 4).getChildNodes().get(0) != null ?
                                tdElementList.get(i + 4).getChildNodes().get(0).asText() : "");
                        companyList.add(company);
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

            //TODO what to do with the alert
            /*
            com.gargoylesoftware.htmlunit.javascript.host.Window alert
            WARNING: window.alert("Search returned more than 300 matching results,
            please refine your search criteria and try again.") no alert handler installed
             */
        }

        return companyList;
    }


}
