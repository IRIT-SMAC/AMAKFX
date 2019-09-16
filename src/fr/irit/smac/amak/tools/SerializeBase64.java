package fr.irit.smac.amak.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 * Tools to convert a serializable object to and from a string using base64.
 * @author Hugo
 *
 */
public class SerializeBase64 {
	
	/**
	 * Serialize into a string using base64
	 * @param o
	 * @return
	 * @throws IOException
	 */
	public static String serialize(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
	}
	
	/**
	 * Deserialze from a string using base64
	 * @param s
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(String s) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream( 
                                        new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
	}
}
