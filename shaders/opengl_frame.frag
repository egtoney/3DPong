#version 130

uniform sampler2D texture_diffuse;

in vec4 pass_Color;
//in vec2 pass_TextureCoord;
in vec4 pass_Bounds;

out vec4 out_Color;

void main(void) {
	//out_Color = set_Color*texture(texture_diffuse, pass_TextureCoord);
	//out_Color = pass_Color;
	//out_Color = vec4( 1, 0, 0, 1 );
	
	if( pass_Bounds != vec4( -1.0 ) ){
		// If the current point is inside of the specified bounds
		if( out_Color[0] >= pass_Bounds[0] &&
				out_Color[0] <= pass_Bounds[0]+pass_Bounds[2] &&
				out_Color[1] >= pass_Bounds[1] &&
				out_Color[1] <= pass_Bounds[1]+pass_Bounds[3])
		{
			out_Color = pass_Color;
		}else{
			out_Color = vec4( 0 );
		}
	}else{
		out_Color = pass_Color;
	}
}