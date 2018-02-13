import java.util.List;
import java.util.Scanner;

/**
 * Simple Console Driven Program
 *
 * Created by josh.taylor on 1/23/2018.
 */
public class ConsoleScraperTester {
    public static void main(String[] args){
        System.out.println("CADO CompanyDetail Search...");
        Scanner input = new Scanner(System.in);

        String choice = "";
        do {
            System.out.println("\n\nSearch by [K]eyword or [B]usiness number or [D]etailed Business Info or [Q]uit:");
            choice = input.next();

            //Keyword Searching
            if(choice.equalsIgnoreCase("K")){
                System.out.println("Keyword 1: ");
                String keyword1 = input.next();

                System.out.println("Performing Search On... " + keyword1);
                printCADOCompanyList(CADOScraper.getCompanyList(keyword1, ""));

                //Business Number Searching
            } else if(choice.equalsIgnoreCase("B")) {
                System.out.println("Enter Business Number:");
                String businessNumber = input.next();

                System.out.println("TODO Search on: " + businessNumber);

                //Not valid input
            } else if(choice.equalsIgnoreCase("D")) {
                System.out.println("Enter Business Number:");
                String businessNumber = input.next();

                System.out.println("TODO Search on: " + businessNumber);

                //Not valid input
            } else {
                System.out.println("Not a valid choice\n");
            }

        } while(!choice.equalsIgnoreCase("q"));

    }

    public static void printCADOCompanyList(List<CADOCompany> companyList){
        for (CADOCompany company : companyList) {
            printCADOCompany(company);
        }

    }

    public static void printCADOCompany(CADOCompany company){
        System.out.println("\n\nPrint Company");
        for (String key: company.getAttributeMap().keySet()){
            System.out.printf("\n%s> %s", key, company.getAttributeValue(key));
        }
    }

}
