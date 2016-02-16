//#version 130 core

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
in vec2 pass_TextureCoord;
in vec4 pass_Dimensions;

out vec4 gl_FragColor;

void main(void) {
	vec4 tex_color = texture(texture_diffuse, pass_TextureCoord);
	
	// If the current point is inside of the specified bounds
	if( gl_FragCoord[0] >= pass_Dimensions[0] &&
			gl_FragCoord[0] <= pass_Dimensions[0]+pass_Dimensions[2] &&
			gl_FragCoord[1] >= pass_Dimensions[1] &&
			gl_FragCoord[1] <= pass_Dimensions[1]+pass_Dimensions[3])
	{
		gl_FragColor = pass_Color*tex_color;
	}else{
		gl_FragColor = vec4( 0, 0, 0, 0 );
	}
}