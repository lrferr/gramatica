package br.com.lrferr.balaogramatica;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Balloon extends Sprite {
	
	private String conteudo;
	private TimerHandler delayOnCreateTimer;

	public Balloon(float f, float g, float timeDelay, final String conteudo, TextureRegion ballonTextureRegion,
			VertexBufferObjectManager vertexBufferObjectManager) {
		// TODO Auto-generated constructor stub
		super(f, g, ballonTextureRegion, vertexBufferObjectManager);
		//delayOnCreateTimer = new TimerHandler(timeDelay, false, new ITimerCallback()
        //{                      
          //  @Override
           // public void onTimePassed(final TimerHandler pTimerHandler)
            //{
				
		Balloon.this.conteudo = conteudo;
				
            //}
			
		//});
		//this.registerUpdateHandler(delayOnCreateTimer);
		
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

}
