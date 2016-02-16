//#version 130 core

in vec3 in_Position;
in vec3 in_Color;
in vec2 in_TextureCoord;

attribute vec4 position;

out vec4 pass_Color;
out vec2 pass_TextureCoord;

void main(void) {
	gl_Position = gl_ModelViewProjectionMatrix * vec4(in_Position, 1);
	
	pass_Color = vec4(in_Color.x, in_Color.y, in_Color.z, 1);
	pass_TextureCoord = in_TextureCoord;
}