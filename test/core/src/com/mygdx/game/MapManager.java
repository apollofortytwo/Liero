package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Rectangle;

public class MapManager {
	Pixmap currentMap;
	public ArrayList<Rectangle> points = new ArrayList<Rectangle>();

	MapManager(Pixmap map) {
		currentMap = map;
	}

	public void looper(int seg) {
		looper(new Rectangle(0, 0, this.currentMap.getWidth(), this.currentMap.getHeight()), seg);
	}

	public void looper(Rectangle rect, int seg) {

		if (this.recttOnlyContainsBlack(currentMap, rect)) {

			if (!points.contains(rect)) {
				points.add(rect);
			}

			return;
		} else if (seg > 0) {
			looper(new Rectangle(rect.x, rect.y, rect.width / 2, rect.height / 2), seg - 1);
			looper(new Rectangle(rect.x + (rect.width / 2), rect.y, rect.width / 2, rect.height / 2), seg - 1);
			looper(new Rectangle(rect.x + (rect.width / 2), rect.y + (rect.height / 2), rect.width / 2,
					rect.height / 2), seg - 1);
			looper(new Rectangle(rect.x, rect.y + (rect.height / 2), rect.width / 2, rect.height / 2), seg - 1);
		}

	}

	private boolean recttOnlyContainsBlack(Pixmap map, Rectangle rectt) {
		for (int x = (int) rectt.x; x < rectt.x + rectt.width; x++) {
			for (int y = (int) rectt.y; y < rectt.y + rectt.height; y++) {
				if (!new Color(map.getPixel(x, y)).equals(Color.BLACK)) {
					return false;
				}
			}
		}
		return true;
	}

}
