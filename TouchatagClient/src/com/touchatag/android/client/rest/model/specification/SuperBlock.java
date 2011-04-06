package com.touchatag.android.client.rest.model.specification;

public class SuperBlock extends Block{

	public Interface interFace;
	public Breakdown breakdown;
	public WiringScheme wiringScheme;
	
	public SuperBlock(){
		nodeName = "superblock";
	}
	
	public String toXml(){
		StringBuilder sb = new StringBuilder();
		sb.append("<" + nodeName);
		if(ref != null){
			sb.append(" ref=\"" + ref + "\"");
		}
		if(id != null){
			sb.append(" id=\"" + id + "\"");
		}
		if(properties.size() > 0){
			sb.append(">");
			for(Property prop : properties){
				sb.append(prop.toXml());
			}
			
		} 
		if(interFace != null){
			
		} else {
			sb.append("<interface/>");
		}
		if(breakdown != null){
			
		} else {
			sb.append("<breakdown/>");
		}
		if(wiringScheme != null){
			
		} else {
			sb.append("<wiringscheme/>");
		}
		sb.append("</" + nodeName + ">");
		return sb.toString();
	}
}
