package org.game;

import java.util.Map;
import java.util.Map.Entry;

public class ItemTransformationTracker {

	public float timeLeft = 0;
	public float duration = 0;
	public Map<String, Integer> product;
	
	public ItemTransformationTracker() {
		
	}
	
	public void update(float deltaTime) {
		if (timeLeft > 0) {
			timeLeft--;
		} else {
			if (product != null) {
				for (Entry<String, Integer> entry : product.entrySet()) {
					var itemType = entry.getKey();
					var count = entry.getValue();
					for (var i = 0; i < count; i++) {
						PlayerData.addToInventory(itemType);
					}
				}
				product = null;
			}
		}
	}

}
