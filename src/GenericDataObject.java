import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * A Generic Data Object that will be built up from scraping. Any child object
 * would define the parameters. This object is a smart hash map that has methods
 * to allow you see if an object has changed.
 *
 *
 * Created by josh.taylor on 2/13/2018.
 */
public abstract class GenericDataObject {

    protected HashMap<String, String> attributeMap = new HashMap<>();
    protected List<String> attributeNameList = new LinkedList<>();

    public GenericDataObject(){
        this.createAttributeNameList();
    }

    /**
     * Return the company value based on nttribute name. Name must be valid and should be defined as a public constant
     * in the data object
     *
     * @param attributeName Name of Value to return - define as public constants
     * @return value
     */
    //TODO What if the name is wrong or there is no value?
    //TODO Throw an Error is more tedious but also less risky than ""
    final public String getAttributeValue(String attributeName) {
        return attributeMap.get(attributeName);
    }

    /**
     * Set an attribute
     *
     * @param attributeName
     * @param value
     */
    final public void setValue(String attributeName, String value) {
        attributeMap.put(attributeName, value);
    }

    /**
     * Get the attribute hash map for comparison
     *
     * @return
     */
    final public HashMap<String, String> getAttributeMap() {
        return this.attributeMap;
    }

    /**
     * Return the list of valid names
     * @return
     */
    final public List<String> getAttributeNameList(){ return this.attributeNameList; }

    /**
     * Make anyone that builds a Data Object specify the attribute names they allow.
     */
    public abstract void createAttributeNameList();

    /**
     * Determine if an Attribute Name is Valid
     * @param attributeName Attribute Name being Used
     * @return True if name is valid
     */
    final protected boolean isValidAttribute(String attributeName){
        for (String attrName: this.attributeNameList) {
            if(attrName.equals(attributeName)){
                return true;
            }
        }
        return false;   //Attribute not found in list - invalid name
    }

    /**
     * Detemine if an object has changed by comparing it to an older (or newer) version.
     * @param oldObject Old version of an object
     * @return
     */
    final public boolean hasChanged(GenericDataObject oldObject){ return sameDataValues(this, oldObject); }

    /**
     * Compares two objects based on Object Type and Attributes
     * @param obj1
     * @param obj2
     */
    //TODO Name this way better!
    final public static boolean sameDataValues(GenericDataObject obj1, GenericDataObject obj2){

        //Make sure you are comparing the same instances
        if(!obj1.getClass().equals(obj2.getClass())){
            return false;
        }

        for (String attribute: obj1.getAttributeNameList()) {
            //TODO what if one is null and one isn't?
            //TODO tighten this up
            if(     //Check for NULL and then do a comparison
                    !(obj1.getAttributeValue(attribute) == null || obj2.getAttributeValue(attribute) == null)
                    &&
                    !obj1.getAttributeValue(attribute).equals(obj2.getAttributeValue(attribute))){
                //TODO Consider characters that may not matter - such as control characters
                //They don't match!
                return false;
            }
        }

        return true;    //No issues found
    }

}
