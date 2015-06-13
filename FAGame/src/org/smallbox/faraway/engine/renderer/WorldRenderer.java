package org.smallbox.faraway.engine.renderer;

import org.smallbox.faraway.GFXRenderer;
import org.smallbox.faraway.Game;
import org.smallbox.faraway.RenderEffect;
import org.smallbox.faraway.SpriteModel;
import org.smallbox.faraway.engine.ui.ColorView;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.manager.WorldManager;
import org.smallbox.faraway.model.GameData;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.model.room.Room;
import org.smallbox.faraway.ui.UserInterface;

import java.util.HashSet;
import java.util.Set;

public class WorldRenderer implements IRenderer {
    private static class Vector2i {
        public final int x;
        public final int y;
        public Vector2i(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private SpriteManager 			_spriteManager;
    private int 					_lastSpecialY;
    private int 					_lastSpecialX;
    private RenderLayer 			_layerStructure;
    //	private RenderLayer 			_layerItem;
    private boolean 				_hasChanged;

    private Set<Vector2i> 			_changed;
    private WorldManager 			_worldMap;
    private MapObjectModel          _itemSelected;
    private int 					_frame;

    public WorldRenderer(SpriteManager spriteManager) {
        _spriteManager = spriteManager;
        _changed = new HashSet<>();

        _layerStructure = ViewFactory.getInstance().createRenderLayer(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);
//		_layerItem = ViewFactory.getInstance().createRenderLayer(Constant.WORLD_WIDTH * Constant.TILE_WIDTH, Constant.WORLD_HEIGHT * Constant.TILE_HEIGHT);

        _hasChanged = true;
    }

    public void onRefresh(int frame) {
        if (_worldMap == null) {
            _worldMap = Game.getWorldManager();
        }

        _frame = frame;
        // TODO
//        int _fromX = Math.max(ui.getRelativePosXMin(0)-1, 0);
//        int _fromY = Math.max(ui.getRelativePosYMin(0)-1, 0);
//		int _toX = Math.min(ui.getRelativePosXMax(Constant.WINDOW_WIDTH)+1, _worldMap.getWidth());
//		int _toY = Math.min(ui.getRelativePosYMax(Constant.WINDOW_HEIGHT)+1, _worldMap.getHeight());
        int fromX = 0;
        int fromY = 0;
        int toX = fromX + 50;
        int toY = fromY + 50;

        if (true || _hasChanged || _changed.size() > 0) {
            _itemSelected = null;

            _layerStructure.clear();

//				for (Vector2i vector: _changed) {
//					refreshFloor(vector.x - 1, vector.y - 1, vector.x + 2, vector.y + 2);
////					refreshResource(vector.x - 1, vector.y - 1, vector.x + 2, vector.y + 2);
//				}

            for (int x = toX; x > fromX; x--) {
                for (int y = toY; y > fromY; y--) {
                    AreaModel area = Game.getWorldManager().getArea(x, y);
                    if (area != null) {
                        if (GameData.config.render.floor) {
                            refreshFloor(area, x, y);
                        }
                        if (GameData.config.render.structure) {
                            refreshStructure(area, x, y);
                        }
                        if (GameData.config.render.resource) {
                            refreshResource(area, x, y);
                        }
                        if (GameData.config.render.item) {
                            refreshItems(area, x, y);
                        }
                        if (GameData.config.render.consumable) {
                            refreshConsumable(area, x, y);
                        }
                    }
                }
            }

            _changed.clear();
            _hasChanged = false;
            _layerStructure.end();
//			_layerItem.end();
        }
    }

    public void onDraw(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        _layerStructure.onDraw(renderer, effect);
        //_layerItem.onDraw(renderer, effect);
    }

    private void refreshResource(AreaModel area, int x, int y) {
        ResourceModel resource = area.getResource();
        if (resource != null) {
            SpriteModel sprite = _spriteManager.getResource(resource);
            if (sprite != null) {
                sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                _layerStructure.draw(sprite);
            }
        }
    }

    // TODO: random
    void	refreshFloor(AreaModel area, int x, int y) {
        StructureModel structure = area.getStructure();

        if (structure != null && structure.isFloor()) {
            if (structure.getName().equals("base.greenhouse")) {
                int index = 2;
                SpriteModel sprite = _spriteManager.getGreenHouse(index + (structure.isWorking() ? 1 : 0));
                sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                _layerStructure.draw(sprite);

                ResourceModel resource = _worldMap.getResource(x, y);
                if (resource != null && resource.getMatterSupply() > 0) {
                    sprite = _spriteManager.getResource(resource);
                    sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                    _layerStructure.draw(sprite);
                }
            }

            // Floor
            else {
                int roomId = 0;
                SpriteModel sprite = _spriteManager.getFloor(structure, roomId, 0);
                if (sprite != null) {
                    sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                    _layerStructure.draw(sprite);
                }

                ResourceModel ressource = _worldMap.getResource(x, y);
                if (ressource != null) {
                    sprite = _spriteManager.getResource(ressource);
                    sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                    _layerStructure.draw(sprite);
                }


//							RectangleShape shape = new RectangleShape(new Vector2f(32, 32));
//							shape.setFillColor(new Color(0, 0, 0, 155 * (12 - area.getLight()) / 12));
//							shape.setPosition(i * Constant.TILE_SIZE, j * Constant.TILE_SIZE);
//							_texture.draw(shape);
            }
        }

        // No floor
        else {
            SpriteModel sprite = _spriteManager.getExterior(x + y * 42, 0);
            sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
            _layerStructure.draw(sprite);
        }
    }

    //TODO: random
    void	refreshStructure(AreaModel area, int x, int y) {
        int offsetWall = (Constant.TILE_WIDTH / 2 * 3) - Constant.TILE_HEIGHT;

        StructureModel structure = area.getStructure();
        if (structure != null) {

            // Door
            if (structure.isDoor()) {
                SpriteModel sprite = _spriteManager.getSimpleWall(0);
                if (sprite != null) {
                    sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT - offsetWall);
                    _layerStructure.draw(sprite);
                }

                sprite = _spriteManager.getItem(structure);
                if (sprite != null) {
                    sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT - 4);
                    _layerStructure.draw(sprite);
                }
            }

            // Floor
            else if (structure.isFloor()) {
            }

            // Wall
            else if (structure.isWall()) {
                SpriteModel sprite = drawWall(structure, x, y, offsetWall);
                _layerStructure.draw(sprite);
            }

            // Hull
            else if (structure.isHull()) {
                SpriteModel sprite = drawWall(structure, x, y, offsetWall);
                _layerStructure.draw(sprite);
            }

            else {
                SpriteModel sprite = SpriteManager.getInstance().getItem(structure);
                sprite.setPosition(structure.getX() * Constant.TILE_WIDTH, structure.getY() * Constant.TILE_HEIGHT);
                _layerStructure.draw(sprite);
            }
        }
    }

    private SpriteModel drawWall(StructureModel item, int i, int j, int offsetWall) {
        SpriteModel sprite = null;

        StructureModel bellow = _worldMap.getStructure(i, j+1);
        StructureModel right = _worldMap.getStructure(i+1, j);
        StructureModel left = _worldMap.getStructure(i-1, j);

        int zone = 0;
        // bellow is a wall
        if (bellow != null && (bellow.isWall() || bellow.isDoor())) {
            StructureModel bellowBellow = _worldMap.getStructure(i, j+2);
            if (bellow.isDoor() || bellowBellow == null || (!bellowBellow.isWall() && !bellowBellow.isDoor())) {
                StructureModel bellowRight = _worldMap.getStructure(i+1, j+1);
                StructureModel bellowLeft = _worldMap.getStructure(i-1, j+1);
                boolean wallOnRight = bellowRight != null && (bellowRight.isWall() || bellowRight.isDoor());
                boolean wallOnLeft = bellowLeft != null && (bellowLeft.isWall() || bellowLeft.isDoor());

                if (wallOnRight && wallOnLeft) {
                    sprite = _spriteManager.getWall(item, 5, 0, zone);
                } else if (wallOnLeft) {
                    boolean wallOnSupRight = right != null && (right.isWall() || right.isDoor());
                    if (wallOnSupRight) {
                        sprite = _spriteManager.getWall(item, 1, 5, zone);
                    } else {
                        sprite = _spriteManager.getWall(item, 5, 2, zone);
                    }
                } else if (wallOnRight) {
                    boolean wallOnSupLeft = left != null && (left.isWall() || left.isDoor());
                    if (wallOnSupLeft) {
                        sprite = _spriteManager.getWall(item, 1, 4, zone);
                    } else {
                        sprite = _spriteManager.getWall(item, 5, 1, zone);
                    }
                } else {
                    sprite = _spriteManager.getWall(item, 5, 3, zone);
                }
            } else {
                boolean wallOnRight = right != null && (right.isWall() || right.isDoor());
                boolean wallOnLeft = left != null && (left.isWall() || left.isDoor());
                if (wallOnRight && wallOnLeft) {
                    sprite = _spriteManager.getWall(item, 1, 0, zone);
                } else if (wallOnLeft) {
                    sprite = _spriteManager.getWall(item, 1, 2, zone);
                } else if (wallOnRight) {
                    sprite = _spriteManager.getWall(item, 1, 1, zone);
                } else {
                    sprite = _spriteManager.getWall(item, 1, 3, zone);
                }
            }

            if (sprite != null) {
                sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
            }
        }

        // No wall above or bellow
        else if (bellow == null || bellow.isWall() == false) {

            // Check double wall
            boolean doubleWall = false;
            if (right != null && right.isComplete() && right.isWall() &&
                    (_lastSpecialY != j || _lastSpecialX != i+1)) {
                StructureModel aboveRight = _worldMap.getStructure(i+1, j-1);
                StructureModel bellowRight = _worldMap.getStructure(i+1, j+1);
                if ((aboveRight == null || aboveRight.isWall() == false) &&
                        (bellowRight == null || bellowRight.isWall() == false)) {
                    doubleWall = true;
                }
            }

            // Normal
            if (bellow == null) {
                // Double wall
                if (doubleWall) {
                    sprite = _spriteManager.getWall(item, 4, i+j, zone);
                    _lastSpecialX = i;
                    _lastSpecialY = j;
                }
                // Single wall
                else {
                    sprite = _spriteManager.getWall(item, 0, 0, zone);
                }
            }
            // Special
            else {
                // Double wall
                if (doubleWall) {
                    sprite = _spriteManager.getWall(item, 2, i+j, zone);
                    _lastSpecialX = i;
                    _lastSpecialY = j;
                }
                // Single wall
                else {
                    sprite = _spriteManager.getWall(item, 3, i+j, zone);
                }
            }
            if (sprite != null) {
                sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
            }
        }

        // // left is a wall
        // else if (left != null && left.type == BaseItem.STRUCTURE_WALL) {
        // 	_spriteManager.getWall(item, 2, &sprite);
        // 	sprite.setPosition(i * TILE_SIZE - TILE_SIZE, j * TILE_SIZE - offset);
        // }

        // single wall
        else {
            sprite = _spriteManager.getWall(item, 0, 0, 0);
            sprite.setPosition(i * Constant.TILE_WIDTH, j * Constant.TILE_HEIGHT - offsetWall);
        }

        return sprite;
    }

    void	refreshItems(AreaModel area, int x, int y) {
        ItemModel item = area.getItem();
        if (item != null && item.getX() == x && item.getY() == y) {

            // Display components
            for (ConsumableModel component: item.getComponents()) {
                SpriteModel sprite = _spriteManager.getItem(component, component.getCurrentFrame());
                if (sprite != null) {
                    if (item.getInfo().storage != null && item.getInfo().storage.components != null) {
                        sprite.setPosition(
                                (x + item.getInfo().storage.components[0]) * Constant.TILE_WIDTH,
                                (y + item.getInfo().storage.components[1]) * Constant.TILE_HEIGHT);
                    } else {
                        sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                    }
                    _layerStructure.draw(sprite);
                }
            }

            // Display crafts
            for (ConsumableModel component: item.getComponents()) {
                SpriteModel sprite = _spriteManager.getItem(component, component.getCurrentFrame());
                if (sprite != null) {
                    if (item.getInfo().storage != null && item.getInfo().storage.crafts != null) {
                        sprite.setPosition(
                                (x + item.getInfo().storage.crafts[0]) * Constant.TILE_WIDTH,
                                (y + item.getInfo().storage.crafts[1]) * Constant.TILE_HEIGHT);
                    } else {
                        sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                    }
                    _layerStructure.draw(sprite);
                }
            }

            // Display item
            SpriteModel sprite = _spriteManager.getItem(item, item.getCurrentFrame());
            if (sprite != null) {
                sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                _layerStructure.draw(sprite);
            }

            // Display selection
            if (item.isSelected()) {
                _itemSelected = item;
            }
        }
    }

    void	refreshConsumable(AreaModel area, int x, int y) {
        ConsumableModel consumable = area.getConsumable();
        if (consumable != null) {

            // Regular item
            SpriteModel sprite = _spriteManager.getItem(consumable, consumable.getCurrentFrame());
            if (sprite != null) {
                sprite.setPosition(x * Constant.TILE_WIDTH, y * Constant.TILE_HEIGHT);
                _layerStructure.draw(sprite);
            }

            // Selection
            if (consumable.isSelected()) {
                _itemSelected = consumable;
            }
        }
    }

    private void refreshSelected(GFXRenderer renderer, RenderEffect effect, int frame, MapObjectModel item) {
        int x = item.getX();
        int y = item.getY();
        int offset = 0;
        switch (frame % 5) {
            case 1: offset = 1; break;
            case 2: offset = 2; break;
            case 3: offset = 3; break;
            case 4: offset = 2; break;
            case 5: offset = 1; break;
        }

        SpriteModel sprite = _spriteManager.getSelectorCorner(0);
        sprite.setPosition(x * Constant.TILE_WIDTH - offset, y * Constant.TILE_HEIGHT - offset);
        renderer.draw(sprite, effect);

        sprite = _spriteManager.getSelectorCorner(1);
        sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, y * Constant.TILE_HEIGHT - offset);
        renderer.draw(sprite, effect);

        sprite = _spriteManager.getSelectorCorner(2);
        sprite.setPosition(x * Constant.TILE_WIDTH - offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
        renderer.draw(sprite, effect);

        sprite = _spriteManager.getSelectorCorner(3);
        sprite.setPosition((x + item.getWidth()) * Constant.TILE_WIDTH - 6 + offset, (y + item.getHeight()) * Constant.TILE_HEIGHT - 6 + offset);
        renderer.draw(sprite, effect);
    }

    public void invalidate(int x, int y) {
//		_changed.add(new Vector2i(x, y));
        _hasChanged = true;
    }

    public void invalidate() {
        _hasChanged = true;
    }

    public void onDrawSelected(GFXRenderer renderer, RenderEffect effect, double animProgress) {
        if (_itemSelected != null) {
            refreshSelected(renderer, effect, _frame, _itemSelected);
        }
    }

}
