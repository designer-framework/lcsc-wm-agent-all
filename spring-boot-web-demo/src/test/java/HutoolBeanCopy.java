import com.lcsc.profiling.web.tests.HutoolCopyTestsService;
import lombok.extern.slf4j.Slf4j;

/**
 * @description:
 * @author: Designer
 * @date : 2024-09-25 22:23
 */
@Slf4j
public class HutoolBeanCopy {

    /**
     * 拷贝对象中的集合(集合中2万个对象)
     *
     * @param args
     * @see cn.hutool.core.convert.ConverterRegistry#convert(java.lang.reflect.Type, java.lang.Object, java.lang.Object, boolean)
     * @see cn.hutool.core.util.ClassLoaderUtil#doLoadClass(java.lang.String, java.lang.ClassLoader, boolean)
     */
    public static void main(String[] args) {
        new HutoolCopyTestsService().copy();
    }

}
