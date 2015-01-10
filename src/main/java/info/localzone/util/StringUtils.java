package info.localzone.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class StringUtils {
static String standardEncoding = "UTF-8";	
	static public String byteToString (byte[] bytes) throws CharacterCodingException
	{
		Charset charset = Charset.forName(standardEncoding);
		CharsetDecoder decoder = charset.newDecoder();
		String string = decoder.decode(ByteBuffer.wrap(bytes)).toString();
		return string;

	}
	
	static public byte[] stringToByte (String string) throws CharacterCodingException {
		Charset charset = Charset.forName(standardEncoding);
		CharsetEncoder encoder = charset.newEncoder();
		CharBuffer charBuffer = CharBuffer.wrap(string.toCharArray());
		return encoder.encode(charBuffer).array();
	
	}
}
