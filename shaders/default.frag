//#version 130 core

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
in vec2 pass_TextureCoord;

out vec4 gl_FragColor;

void main(void) {
	gl_FragColor = pass_Color;
	// Override out_Color with our texture pixel
	gl_FragColor = texture(texture_diffuse, pass_TextureCoord);
}