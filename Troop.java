import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.lang.Math;
/**
 * This class encapsulates a network players
 * @author Joseph Anthony C. Hermocilla
 *
 */

public class Troop implements Constants{
	private String type; //class of troop: Archer (a), Barbarian (b), or Horseman (h)
	private int owner; //player who owns the troop
	private int x,y,hp; //troop position
	private int x1,x2,y1,y2; //The bounds of base
	private int midX, midY; //location of castle
	//public static LinkedList<Troop> deployedTroops = new LinkedList<Troop>();
	private BufferedImage[] sprites = {
		Sprite.getSprite(0, 0, 1), //Stand
		Sprite.getSprite(0, 3, 1), Sprite.getSprite(0, 4, 1), //walkLeft
		Sprite.getSprite(0, 1, 1), Sprite.getSprite(0, 2, 1), //walkRight
		Sprite.getSprite(0, 7, 1), Sprite.getSprite(0, 8, 1), //walkUp
		Sprite.getSprite(0, 5, 1), Sprite.getSprite(0, 6, 1), //walkDown
		Sprite.getSprite(0, 9, 1), Sprite.getSprite(0, 0, 1) //Attack
	};
	private int spriteIndex = 8;
	public Troop(String data){
		String tokens[] = data.split(" ");
		this.owner = Integer.parseInt(tokens[1]);
		this.type = tokens[2];
		this.x = Integer.parseInt(tokens[3]);
		this.y = Integer.parseInt(tokens[4]);

		//upper left base
		if(this.owner == 0){
			this.x1 = 0;
			this.x2 = 406;
			this.y1 = 0;
			this.y2 = 715;
			this.midX = 153;
			this.midY = 308;

		//upper right base
		}else if(this.owner == 1){
			this.x1 = 407;
			this.x2 = 885;
			this.y1 = 0;
			this.y2 = 435;
			this.midX = 596;
			this.midY = 168;

		//lower right base
		}else if(this.owner == 2){
			this.x1 = 407;
			this.x2 = 885;
			this.y1 = 436;
			this.y2 = 715;
			this.midX = 596;
			this.midY = 523;
		}
		

		this.hp = 60;
		
		//change Sprite according to owner and type/class
		int spriteSheet = 0;
		
		if(type.equals("a")){
			spriteSheet = 0;
			this.hp = 50;
		}
		else if(type.equals("b")){
			spriteSheet = 1;
			this.hp = 50;
		}
		else if(type.equals("h")){
			spriteSheet = 2;
			this.hp = 50;
		}
		sprites[0] = Sprite.getSprite(spriteSheet, 0, this.owner);
		sprites[1] = Sprite.getSprite(spriteSheet, 3, this.owner);
		sprites[2] = Sprite.getSprite(spriteSheet, 4, this.owner);
		sprites[3] = Sprite.getSprite(spriteSheet, 1, this.owner);
		sprites[4] = Sprite.getSprite(spriteSheet, 2, this.owner);
		sprites[5] = Sprite.getSprite(spriteSheet, 7, this.owner);
		sprites[6] = Sprite.getSprite(spriteSheet, 8, this.owner);
		sprites[7] = Sprite.getSprite(spriteSheet, 5, this.owner);
		sprites[8] = Sprite.getSprite(spriteSheet, 6, this.owner);
		sprites[9] = Sprite.getSprite(spriteSheet, 9, this.owner);
		sprites[10] = Sprite.getSprite(spriteSheet, 0, this.owner);
		
		if(tokens.length > 5){
			this.hp = Integer.parseInt(tokens[5]);
			this.spriteIndex = Integer.parseInt(tokens[6]);
		}
			
	}
	public void moveLeft(){
		x = x - 10;
		if(spriteIndex != 1) spriteIndex = 1;
		else spriteIndex = 2;
	}
	
	public void moveRight(){
		x = x + 10;
		if(spriteIndex != 3) spriteIndex = 3;
		else spriteIndex = 4;
	}
	
	public void moveUp(){
		y = y - 10;
		if(spriteIndex != 5) spriteIndex = 5;
		else spriteIndex = 6;
	}
	
	public void moveDown(){
		y = y + 10;
		if(spriteIndex != 7) spriteIndex = 7;
		else spriteIndex = 8;	
	}
	
	public void stand(){
		spriteIndex = 0;
	}
	
	public void attack(){
		if(spriteIndex != 9) spriteIndex = 9;
		else spriteIndex = 10;
	}
	
	public void decide(){
		if(hp > 0){
			//troops currently deployed in the game
			LinkedList<Troop> deployedTroops = GameServer.getDeployedTroops();
			LinkedList<NetPlayer> players = GameServer.getPlayers();
			//troop located on own base
			if((x>this.x1 && x<this.x2) && (y>this.y1 && y<this.y2)){
				defendCastle(deployedTroops);
			}else{

				//troop on base1
				if((x>0 && x<406) && (y>0 && y<715)){
					//location of castle on base 1
					if(NUMBER_OF_PLAYERS>0 && players.get(0).getHP() > 0){
						if(x>115 && x<250 && y>255 && y<406){
							this.attack();
							players.get(0).damage(15);
						}
						else if(x<=115) this.moveRight();
						else if(x>=250) this.moveLeft();
						else if(y<=255) this.moveDown();
						else if(y>=406) this.moveUp();
					}
					else{
						this.stand();
					}
				}

				//troop on base2
				else if((x>407 && x<885) && (y>0 && y<435)){
					//location of castle on base 2
					/*if(x > 596){ 
						this.attack();
						players.get(1).damage(15);
					}
					else{
						
						this.moveRight();
					}*/
					if(NUMBER_OF_PLAYERS>1 && players.get(1).getHP() > 0){
						if(x>558 && x<693 && y>121 && y<266){
							this.attack();
							players.get(1).damage(15);
						}
						else if(x<=558) this.moveRight();
						else if(x>=693) this.moveLeft();
						else if(y<=121) this.moveDown();
						else if(y>=266) this.moveUp();
					}
					else{
						this.stand();
					}

				//troop on base3
				}else if((x>407 && x<885) && (y>436 && y<715)){
					//location of castle on base 3
					if(NUMBER_OF_PLAYERS==3 && players.get(2).getHP() > 0){
						if(x>558 && x<693 && y>476 && y< 621){
							this.attack();
							players.get(2).damage(15);
						}
						else if(x<=558) this.moveRight();
						else if(x>=693) this.moveLeft();
						else if(y<=476) this.moveDown();
						else if(y>=621) this.moveUp();
					}
					else{
						this.stand();
					}
				}
			}
		}
	}


	public boolean collisionExist(LinkedList<Troop> deployedTroops){

		//check if there exist a troop in a coordinate
		for(Troop troop: deployedTroops){
			if((this.x == troop.x) && (this.y == troop.y)){
				return true;
			}
		}return false;
	}
	public void getDamage(int damage){
		this.hp = this.hp-damage;
	}
	public void defendCastle(LinkedList<Troop> deployedTroops){
		for(Troop troop: deployedTroops){
			if((troop.owner != this.owner) && (troop.isAlive()) && (troop.x>this.x1 && troop.x<this.x2) && (troop.y>this.y1 && troop.y<this.y2) ){
				if(Math.abs(troop.x - this.x) <= 20 && Math.abs(troop.y - this.y) <= 20){
					this.attack();
					troop.getDamage(15);
					this.getDamage(5);
				}
				else if(Math.abs(troop.x - this.x) > 20){
					if(troop.x > this.x){
						this.moveRight();
					}
					else{
						this.moveLeft();
					}
				}
				else if(Math.abs(troop.y - this.y) > 20){
					if(troop.y > this.y){
						this.moveDown();
					}
					else{
						this.moveUp();
					}
				}
			return;
			}
		}
		this.spriteIndex = 0;	
		return;
	}

	public String toString(){
		return owner + " " + type + " " + x + " " + y + " " + hp + " " +spriteIndex;
	}
	public boolean isAlive(){
		if(this.hp>0) return true;
		return false;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getOwner(){
		return owner;
	}
	public BufferedImage getSprite(){
		return sprites[spriteIndex];
	}
}
