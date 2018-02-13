import java.util.HashMap;

/**
 * CADO Company Information
 *
 * Created by josh.taylor on 1/23/2018.
 */
public class CADOCompany extends GenericDataObject {

    public static final String NAME = "Name";
    public static final String NUMBER = "Number";
    public static final String STATUS = "Status";
    public static final String CORPORATION_TYPE = "CorporationType";
    public static final String INCORPORATION_DATE = "IncorporationDate";


    @Override
    public void createAttributeNameList() {
        this.attributeNameList.add(NAME);
        this.attributeNameList.add(NUMBER);
        this.attributeNameList.add(STATUS);
        this.attributeNameList.add(CORPORATION_TYPE);
        this.attributeNameList.add(INCORPORATION_DATE);
    }
}
