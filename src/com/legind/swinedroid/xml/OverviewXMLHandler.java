package com.legind.swinedroid.xml;

import java.io.IOException;

import org.achartengine.chartlib.AlertChart;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class OverviewXMLHandler extends XMLHandler{
	private boolean in_all_time = false;
	private boolean in_last_72 = false;
	private boolean in_last_24 = false;
	private boolean in_high = false;
	private boolean in_medium = false;
	private boolean in_low = false;
	private boolean in_graph_info = false;
	private boolean in_label = false;
	public OverviewXMLElement current_element = new OverviewXMLElement();
	public AlertChart alertChart;
	
	@Override
	public void startElement(String uri, String name, String qName, Attributes atts){
		super.startElement(uri, name, qName, atts);
		if(name.trim().equals("graph_info")){
			alertChart.addAlertMoment();
			in_graph_info = true;
		}
		if(name.trim().equals("label")){
			in_label = true;
		}
		if(name.trim().equals("all_time")){
			in_all_time = true;
		}
		if(name.trim().equals("last_72")){
			in_last_72 = true;
		}
		if(name.trim().equals("last_24")){
			in_last_24 = true;
		}
		if(name.trim().equals("high")){
			in_high = true;
		}
		if(name.trim().equals("medium")){
			in_medium = true;
		}
		if(name.trim().equals("low")){
			in_low = true;
		}
	}

	@Override
	public void endElement(String uri, String name, String qName){
		super.endElement(uri, name, qName);
		if(name.trim().equals("graph_info")){
			in_graph_info = false;
		}
		if(name.trim().equals("label")){
			in_label = false;
		}
		if(name.trim().equals("all_time")){
			in_all_time = false;
		}
		if(name.trim().equals("last_72")){
			in_last_72 = false;
		}
		if(name.trim().equals("last_24")){
			in_last_24 = false;
		}
		if(name.trim().equals("high")){
			in_high = false;
		}
		if(name.trim().equals("medium")){
			in_medium = false;
		}
		if(name.trim().equals("low")){
			in_low = false;
		}
	}
	
	@Override
	public void characters(char ch[], int start, int length){
		super.characters(ch, start, length);
		String chars = (new String(ch).substring(start, start + length));
		if(in_graph_info && in_high){
			alertChart.setLastMomentHighAlert(Integer.parseInt(chars));
		}
		if(in_graph_info && in_medium){
			alertChart.setLastMomentMediumAlert(Integer.parseInt(chars));
		}
		if(in_graph_info && in_low){
			alertChart.setLastMomentLowAlert(Integer.parseInt(chars));
		}
		if(in_graph_info && in_label){
			alertChart.setLastMomentLabel(chars);
		}
		if(in_all_time && in_high)
			current_element.all_time_high = Integer.parseInt(chars);
		if(in_all_time && in_medium)
			current_element.all_time_medium = Integer.parseInt(chars);
		if(in_all_time && in_low)
			current_element.all_time_low = Integer.parseInt(chars);
		if(in_last_72 && in_high)
			current_element.last_72_high = Integer.parseInt(chars);
		if(in_last_72 && in_medium)
			current_element.last_72_medium = Integer.parseInt(chars);
		if(in_last_72 && in_low)
			current_element.last_72_low = Integer.parseInt(chars);
		if(in_last_24 && in_high)
			current_element.last_24_high = Integer.parseInt(chars);
		if(in_last_24 && in_medium)
			current_element.last_24_medium = Integer.parseInt(chars);
		if(in_last_24 && in_low)
			current_element.last_24_low = Integer.parseInt(chars);
	}
	
	@Override
	public void createElement(String username, String password, String call) throws IOException, SAXException, XMLHandlerException{
		alertChart = new AlertChart();
		super.createElement(username, password, call);
	}
}