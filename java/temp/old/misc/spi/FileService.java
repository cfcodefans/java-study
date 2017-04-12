package cf.study.misc.spi;

import java.io.File;

public class FileService implements IService {
	public Boolean service(Object...params) {
		return (new File((String)params[0])).exists();
	}
}
