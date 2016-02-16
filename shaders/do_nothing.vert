attribute vec4 position;
// the vertex position and color are passed from an opengl program
attribute vec4 color;

varying vec4 pcolor;
// this is the output variable to the fragment shader
// this shader just pass the vertex position and color along, doesn't actually do anything
// Note that this means the vertex position is assumed to be already in clip space

void main(){
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	pcolor = gl_Color;
}