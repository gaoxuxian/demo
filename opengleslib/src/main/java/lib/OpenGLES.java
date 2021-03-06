package lib;

/**
 * 记录学习 OpenGL ES 2.0 网络资料
 */

public interface OpenGLES
{
    /**
     * 基础教程
     * https://blog.piasy.com/2016/06/07/Open-gl-es-android-2-part-1/
     * https://blog.piasy.com/2016/06/14/Open-gl-es-android-2-part-2/
     * https://blog.piasy.com/2017/10/06/Open-gl-es-android-2-part-3/
     *
     *
     * OpenGL ES20 API 英文在线文档(需翻墙)
     * http://www.khronos.org/registry/OpenGL-Refpages/es2.0/
     *
     *
     * OpenGL 英文教程(理解概念、方法参数比较不错)(需翻墙)
     * http://blog.db-in.com/all-about-opengl-es-2-x-part-1/
     *
     *
     * 小码哥_WS 大神(很多 ffmpeg、OpenGL 资料)
     * http://blog.csdn.net/king1425
     *
     *
     * 松阳 大神 (很多 c代码写的 着色器)
     * http://blog.csdn.net/fansongy/article/list?viewmode=contents
     *
     *
     * 重点!!!!!!
     * 解释 使用 GLSurface view 时，为何绑定纹理id 之后，使用 OpenGL 渲染，可以直接渲染到 GLSurface view 的 surface 上 ( 关键点：eglMakeCurrent() )
     * <p>
     *     <该博客上还附有其他 Android 游戏开发的大神博客>
     * <p>
     * http://blog.csdn.net/happy19850920
     *
     *
     * 重点！！！
     * OpenGL 顶点,坐标系,纹理坐标
     * http://blog.csdn.net/tom_221x/article/details/38454753
     *
     *
     * 飘飘白云 博客，资料解释比较详细
     * http://www.cnblogs.com/kesalin/archive/2012/12/06/3D_math.html
     *
     *
     * =====================   比较好的学习资料  ===========================
     *
     *    重点!!!!!!
     * 1、湖广午王 的博客 (有比较全面的 OpenGL ES20 的系列教程)
     *    http://blog.csdn.net/junzia/article/list
     *
     *    LearnOpenGL教程的中文翻译
     *    https://learnopengl-cn.readthedocs.io/zh/latest/
     *
     *    游戏GL 教程
     *    https://glumes.com/categories/opengl/page/3/
     *
     *    重点 ！！！！ 对于 齐次方程 等概念理解
     * 2、什么是「齐次」，「非齐次」，「线性」，「非线性」？
     *    https://www.zhihu.com/question/19816504
     *
     *    重点 ！！！！
     * 3、维基百科 -- 矩阵
     *    https://zh.wikipedia.org/wiki/%E7%9F%A9%E9%98%B5#%E7%BA%BF%E6%80%A7%E6%96%B9%E7%A8%8B%E7%BB%84
     * 3-1、
     * -> 在中国大陆，横向的元素组称为“行”，纵向称为“列”，而在台湾则相反
     * -> 矩阵乘法 ( 矩阵乘法不满足交换律。一般来说，矩阵A及B的乘积AB存在，但BA不一定存在，即使存在，大多数时候AB ≠ BA )
     * -> 两个矩阵的乘法仅当第一个矩阵A的列数(column)和另一个矩阵B的行数(row)相等时才能定义。如A是m×n矩阵和B是n×p矩阵，它们的乘积AB是一个m×p矩阵
     *
     *    重点！！！！！
     * 4、GLSL 语言(OpenGL Shader Language)
     *    https://www.jianshu.com/p/8a9fbd857188
     *
     *    着色器 基础知识
     *    https://www.jianshu.com/p/eea423753fb0
     *
     * 5、
     *  GlSurface View 工作原理初理解
     *  https://www.jianshu.com/p/2414c8c09843
     *
     * 6、天天P图 android 讲解混合模式的贴
     *  https://cloud.tencent.com/developer/article/1132385
     *
     * 7、UV 坐标的理解
     *  https://www.cnblogs.com/jiahuafu/p/5942228.html
     *
     * 8、EGL 相关信息、概念
     *  8-1、https://woshijpf.github.io/android/2017/09/04/Android%E7%B3%BB%E7%BB%9F%E5%9B%BE%E5%BD%A2%E6%A0%88OpenGLES%E5%92%8CEGL%E4%BB%8B%E7%BB%8D.html
     *
     *  8-2、http://gad.qq.com/article/detail/14401
     *
     * 9、滤镜-Color Lookup Table(ColorLUT)技术
     *  https://blog.csdn.net/danffer1985/article/details/78692992
     *
     * 10、找 LUT 原图时，找到的一些资料
     *  https://zhuanlan.zhihu.com/p/43241990
     *
     *  https://www.jianshu.com/p/7d25add74831
     *
     *  https://blog.csdn.net/u010468553/article/details/79456447
     *
     *  https://www.cnblogs.com/Anita9002/p/8426711.html
     *
     *  https://zhuanlan.zhihu.com/p/26644905 -- 有 shader 算法
     *
     *  11、自定义滤镜，类似抖音效果，有shader算法讲解
     *   http://z-xj.com/2017/09/30/%E8%87%AA%E5%AE%9A%E4%B9%89GPUImageFilter%E6%BB%A4%E9%95%9C%EF%BC%88%E7%B1%BB%E4%BC%BC%E6%8A%96%E9%9F%B3%E6%95%88%E6%9E%9C%EF%BC%89/
     *
     *  12、有各种滤镜效果
     *   https://www.kancloud.cn/trent/hotoimagefilter/102795
     *
     *  13、图片处理大神blog
     *   https://blog.csdn.net/Trent1985/article/details/81164425
     *
     *  14、快速模糊算法(暂时看不懂)
     *   http://www.cnblogs.com/Imageshop/p/6849611.html
     *
     *  15、图像处理课题研究、图像滤镜艺术
     *   https://www.kancloud.cn/trent/imagesharp/100473
     *   https://www.kancloud.cn/trent/hotoimagefilter/102807
     *
     *  16、
     *   glrotatef如何绕自身轴旋转
     *   https://blog.csdn.net/ly763124994/article/details/20161313
     *
     *
     * =====================  透视投影的理解过程  =========================== 可以在 assets\glDemo\ 下结合demo效果去理解
     *
     * OpenGL Transformation (最全的资料，而且自带一些zip包 demo，可运行查看 open gl 的画图过程，不过是英文博客, 下面的资料大多是根据这份博客进行翻译解释)
     * http://www.songho.ca/opengl/gl_transform.html
     *
     * 上述链接的前置博客(同一个博主)
     * http://www.songho.ca/opengl/gl_projectionmatrix.html
     *
     * ~~~~~~~ 以下记录的与上述链接博客不是同一个博主，只是对上述博客的一些翻译、解释
     *
     * ~OpenGL---gluLookAt函数详解
     * https://blog.csdn.net/ivan_ljf/article/details/8764737
     *
     * ~OpenGL学习脚印: 投影矩阵和视口变换矩阵(math-projection and viewport matrix)
     * https://blog.csdn.net/wangdingqiaoit/article/details/51589825
     *
     * ~OpenGL 学习系列---投影矩阵 (解释了为什么 透视矩阵中 left 、bottom 一定要传 负数，right、top 一定是 正数)
     * https://glumes.com/post/opengl/opengl-tutorial-projection-matrix/
     *
     *
     * ==================== 额外的一些知识 ( 踩坑过程遇到 ) =============================
     *
     * OGL纹理之纹理替换glTexSubImage2D/glCopyTexSubImage2D/纹理矩形
     * https://blog.csdn.net/Blues1021/article/details/52311621
     */
}
