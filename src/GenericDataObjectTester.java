/**
 * Created by josh.taylor on 2/13/2018.
 */
public class GenericDataObjectTester {

    public static void main(String[] args){
        System.out.println("Testing the Generic Data Objects!");

        CADOCompany cadoCompany1 = new CADOCompany();
        CADOCompany cadoCompany2 = new CADOCompany();

        System.out.printf("Compare two empty objects. Test outcome: %b\n", GenericDataObject.sameDataValues(cadoCompany1, cadoCompany2));


    }
}
