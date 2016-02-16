varying vec4 pcolor;

// Need to match the name with the desired variable from varying vec4 pcolor; 

// the vertex program
//
// This fragment shader just passes the already interpolated fragment color 
//

void main() {
	gl_FragColor = vec4(1, 0, 0, 1); // note that pcolor; 
	// note that gl FragColor gl_FragColor is a default name for is a default name for
	// the final fragment color
} 