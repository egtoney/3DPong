#version 130

in vec3 in_Position;
in vec3 in_Normal;
in vec3 in_Color;

out vec4 pass_Color;

void main(){
	gl_Position = gl_ModelViewProjectionMatrix * vec4(in_Position, 1);
	pass_Color = vec4( in_Color, 1 );
}