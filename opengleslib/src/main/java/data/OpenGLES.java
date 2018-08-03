package data;

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
     * 重点!!!!!!
     * 湖广午王 的博客 (有比较全面的 OpenGL ES20 的系列教程)
     * http://blog.csdn.net/junzia/article/list
     *
     *
     * GL ES 矩阵变换
     * http://blog.csdn.net/jamesshaoya/article/details/54342241
     *
     *
     * 重点!!!!!!
     * 着色器 基础知识
     * https://www.jianshu.com/p/eea423753fb0
     *
     *
     * 简单解释了正交投影、透视投影
     * http://www.cnblogs.com/ghj1976/archive/2012/04/27/2473624.html
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
     * 重点 ！！！！ 关于矩阵变换的理解
     * 关于 Android Matrix
     * https://www.jianshu.com/p/42d8aceb3136
     *
     * 重点 ！！！！ 对于 齐次方程 等概念理解
     * 什么是「齐次」，「非齐次」，「线性」，「非线性」？
     * https://www.zhihu.com/question/19816504
     *
     * 重点 ！！！！
     * 维基百科 -- 矩阵
     * https://zh.wikipedia.org/wiki/%E7%9F%A9%E9%98%B5#%E7%BA%BF%E6%80%A7%E6%96%B9%E7%A8%8B%E7%BB%84
     *
     * 重点！！！！！
     * glsl 语言
     * https://www.jianshu.com/p/8a9fbd857188
     *
     *
     * 飘飘白云 博客，资料解释比较详细
     * http://www.cnblogs.com/kesalin/archive/2012/12/06/3D_math.html
     *
     *
     *
     *
     * 各种坐标轴分析
     * https://www.jianshu.com/p/355137fa2817
     *
     *
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
     */
}
