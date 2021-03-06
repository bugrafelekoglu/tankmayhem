package game;

import gui.core.MouseEvent;
import processing.core.PGraphics;
import processing.event.KeyEvent;

/**
 *
 * @author Burak Gök
 */
public class FireInteraction extends Interaction {
    
    /** Id of the bomb that will be generated */
    private int itemId;
    
    public FireInteraction(Game game) {
        super(game);
    }
     
    void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    @Override
    public String getAction() {
        return "FIRE";
    }

    private final float maxPower = 100;
    
    @Override
    public void drawBehindTerrain(PGraphics g) {
        g.ellipseMode(PGraphics.RADIUS);
        g.fill(0, 0, 255, 70);
        g.noStroke();
        g.ellipse(tank.getBarrelX(), tank.getBarrelY(), maxPower, maxPower);
    }

    @Override
    public void drawAfterTerrain(PGraphics g) {
//        g.ellipseMode(PGraphics.RADIUS);
        g.fill(0, 0, 255, 120);
//        g.noStroke();
        float power = maxPower * tank.firePower;
        float angle = tank.fireAngle + tank.getRotation();
        g.arc(tank.getBarrelX(), tank.getBarrelY(), power, power,
                angle - 0.3f, angle + 0.3f);
    }

    @Override
    public void _finalize() {
        super._finalize();
        
        Bomb bomb = (Bomb) game.getCatalog().create(itemId);
//        Bomb bomb = new SimpleBomb(game); // Test, passed
//        Bomb bomb = new BouncingBomb(game); // Test, passed
//        Bomb bomb = new OneBounceBomb(game); // Test, passed
//        Bomb bomb = new VolcanoBomb(game); // Test, passed
//        bomb.blastPower = 60; // Test
        
        float angle = tank.fireAngle + tank.getRotation();
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        float x = tank.getBarrelX() + 40 * cos; // TODO 40: barrel width
        float y = tank.getBarrelY() + 40 * sin;
        float velX = 1500 * tank.firePower * cos;
        float velY = 1500 * tank.firePower * sin;
        
        bomb.init(x, y, velX, velY);
        bomb.blastPower *= tank.getDamageBonus();
        game.addEntity(bomb);
        
        game.getCurrentPlayer().getInventory().remove(itemId);
        game.switchTurnWhenStabilized(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (e.getKeyCode() == 37) // <-
            tank.fireAngle = (float)Math.max(tank.fireAngle - 0.05, -Math.PI);
        else if (e.getKeyCode() == 39) // ->
            tank.fireAngle = (float)Math.min(tank.fireAngle + 0.05, 0);
        else if (e.getKeyCode() == 38) // Up
            tank.firePower = (float)Math.min(tank.firePower + 0.02, 1);
        else if (e.getKeyCode() == 40) // Down
            tank.firePower = (float)Math.max(tank.firePower - 0.02, 0);
    }
    
    @Override
    public boolean mousePressed(MouseEvent e) {
        return mouseDragged(e);
    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        float x = tank.getBarrelX() - camBounds[0]; // tank x on component
        float y = tank.getBarrelY() - camBounds[1]; // tank y on component
        float dx = e.getX() - x;
        float dy = e.getY() - y;
        
        double angle = Math.atan2(dy, dx) - tank.getRotation();
        if (angle > 0) angle -= 2 * Math.PI; // [0, -pi; pi, 0] -> [0, -2pi]
        if (angle < -Math.PI && angle > -2 * Math.PI)
            return true;
        tank.fireAngle = (float)angle;
        
        double dist = Math.sqrt(dx * dx + dy * dy);
        tank.firePower = (float)Math.min(dist / maxPower, 1);
        return true;
    }
    
}
