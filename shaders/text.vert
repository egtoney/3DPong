#version 130

uniform vec4 set_Color;

in vec3 in_Position;
in vec2 in_TextureCoord;

attribute vec4 position;

out vec4 pass_Color;
out vec2 pass_TextureCoord;

void main(void) {
	gl_Position = gl_ModelViewProjectionMatrix * vec4(in_Position, 1);
	
	pass_Color = set_Color;
	pass_TextureCoord = in_TextureCoord;
}