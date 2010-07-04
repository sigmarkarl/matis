package org.netbeans.saas;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

public class RestResponse extends CompatResponse {
    public <T> T getDataAsObject(Class<T> jaxbClass) throws JAXBException {
        return getDataAsObject(jaxbClass, jaxbClass.getPackage().getName());
    }
 
    public <T> T getDataAsObject(Class<T> clazz, String packageName) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(packageName);
        Unmarshaller u = jc.createUnmarshaller();
        Object obj = u.unmarshal(new StreamSource(new StringReader(getDataAsString())));
        
        if (obj instanceof JAXBElement) {
            return (T) ((JAXBElement) obj).getValue();
        } else {
            return (T) obj;
        } 
    }
}
