#version 130

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
//in vec2 pass_TextureCoord;
in vec4 pass_Bounds;

out vec4 gl_FragColor;

void main(void) {
	//gl_FragColor = set_Color*texture(texture_diffuse, pass_TextureCoord);
	//gl_FragColor = pass_Color;
	//gl_FragColor = vec4( 1, 0, 0, 1 );
	
	if( pass_Bounds != vec4( -1.0 ) ){
		// If the current point is inside of the specified bounds
		if( gl_FragCoord[0] >= pass_Bounds[0] &&
				gl_FragCoord[0] <= pass_Bounds[0]+pass_Bounds[2] &&
				gl_FragCoord[1] >= pass_Bounds[1] &&
				gl_FragCoord[1] <= pass_Bounds[1]+pass_Bounds[3])
		{
			gl_FragColor = pass_Color;
		}else{
			gl_FragColor = vec4( 0 );
		}
	}else{
		gl_FragColor = pass_Color;
	}
}