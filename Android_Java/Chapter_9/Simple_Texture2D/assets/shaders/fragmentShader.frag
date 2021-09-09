#version 300 es
precision mediump float;
in vec2 v_texCoord;
layout(location = 0) out vec4 outColor;
// 声明一个类型为sampler2D的统一变量s_texture。
// 采样器是用于从纹理贴图中读取的特殊统一变量。
// 采样器统一变量将加载一个指定纹理绑定的纹理单元的数值。例如：
//    用数值0指定采样器表示从单元GL_TEXTURE0读取
//    用数值1指定采样器表示从单元GL_TEXTURE1读取，以此类推。
uniform sampler2D s_texture;
void main()
{
	// 使用内建函数texture，以法线作为纹理坐标从立方图中读取
	// 内建函数采用如下形式：
	// vec4  texture(sampler2D sampler, vec2 coord[,float bias]);
	// @param sampler 绑定到纹理单元的采样器，指定纹理为读取来源
	// @param coord   用于从纹理贴图中读取的2D纹理坐标
	// @param bias  可选参数，提供用于纹理读取的mip贴图偏置。这允许着色器明确地偏置用于mip贴图选择的LOD的计算值
	outColor = texture( s_texture, v_texCoord );
}