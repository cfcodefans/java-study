package cf.study.data.mining;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by Administrator on 2016/6/28.
 */
public class ClazzCollector {
	static class ClazzReader extends ClassVisitor {

		public ClazzReader() {
			super(Opcodes.ASM4);
		}
	}
}
