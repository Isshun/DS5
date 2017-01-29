void main()
{
    //Compute level of gray
    float gray = dot(gl_Color.rgb, vec3(0.299, 0.587, 0.114));

    //Write destination color
    gl_FragColor = vec4(gray, gray, gray, gl_Color.a);
}