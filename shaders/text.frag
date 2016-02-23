#version 130

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

void main(void) {
	vec4 tex_color = texture(texture_diffuse, pass_TextureCoord);
	
	gl_FragColor = pass_Color*tex_color;
}