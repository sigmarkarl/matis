package org.netbeans.saas;


public class RestResponse extends CompatResponse {
    public <T> T getDataAsObject(Class<T> jaxbClass) {
        return null;//getDataAsObject(jaxbClass, jaxbClass.getPackage().getName());
    }
 
    public <T> T getDataAsObject(Class<T> clazz, String packageName) {
        /*JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        Object obj = u.unmarshal(new StreamSource(new StringReader(getDataAsString())));
        
        if (obj instanceof JAXBElement) {
            return (T) ((JAXBElement) obj).getValue();
        } else {
            return (T) obj;
        }*/
    	return null;
    }
}
