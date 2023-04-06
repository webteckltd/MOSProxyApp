package com.o2.manonsite.mrs;

import java.util.ArrayList;
import java.util.List;




public class RequestContent {
	 protected List<Item> item;

	public List<Item> getItem() {
		return item;
	}

	public void setItem(List<Item> item) {
		this.item = item;
	}

	@Override
	public String toString() {
		return "RequestContent [item=" + item + "]";
	}
	
	public synchronized void  addItem(Item item1){
		if (null !=this.item){
			this.item.add(item1);
		}else{
			this.item  =  new ArrayList<Item>();
			this.item.add(item1);
		}	
	}
	
	
	public synchronized void  addItem(Item[] item1){
		if (null !=this.item){
			for (int i = 0; i < item1.length; i++) {
				this.item.add(item1[i]);
			}
			
			
		}else{
			this.item  =  new ArrayList<Item>();
			for (int i = 0; i < item1.length; i++) {
				this.item.add(item1[i]);
			}
		}	
	}
	
	 
	 
}
