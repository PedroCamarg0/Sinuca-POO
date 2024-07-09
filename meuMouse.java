
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class meuMouse extends MouseAdapter{
    JSlider slider;
 
    Bola bolaBranca;
    double energia;
    double forca=0;
    LinkedList<Bola> bolas;

    //Construtor do Listener
    meuMouse(Bola bolaBranca, JSlider slider, LinkedList<Bola> bolas){
        this.bolaBranca = bolaBranca;
        this.slider = slider;
        this.bolas = bolas;
    }
    
    //Recebe a soma das energias das bolas na mesa
    public void setEnergia(double energia){
        this.energia = energia;
    }
    
    //Executa ações quando o mause é movido
    public void mouseMoved(MouseEvent e){
        
        //Move a bola  com o mouse quando é derrubada em uma caçapa
        if(bolaBranca.caiu){
            bolaBranca.posicao=new Vetor(e.getX(), e.getY());
            
            //Impede de posicionar a bola branca dentro de outras
            for(Bola b: bolas){
                Vetor distancia = Bola.distancia(bolaBranca,b);

                if(distancia.norma<=Constantes.RAIO*2){
                    double correcao = (bolaBranca.raio*2-distancia.norma)+1;
                    bolaBranca.posicao = Vetor.soma(bolaBranca.posicao, Vetor.multiplicacao(distancia.diretor(), -correcao));
                    
                }
            }

        }

    }

    //Executa ações quando o botão do mouse é pressionado
    public void mouseClicked(MouseEvent e){

        //Tacada:
        //  Lança a bola na direção do mouse com a força escolhida no slider
        if(!bolaBranca.caiu && energia==0){
            Vetor mousePos = new Vetor(e.getX(), e.getY());
            Vetor distancia = Vetor.subtracao(mousePos, bolaBranca.posicao);
            bolaBranca.velocidade = Vetor.multiplicacao(distancia.diretor(), slider.getValue()/30.0);
            slider.setValue(0);
            bolaBranca.primeiraColisao = true;
        }

        //Reposiciona a bola branca no local selecionado
        if(bolaBranca.caiu && energia==0){
            Boolean dentro=false;

            //Verifica se a bola branca está dentro de outra
            for(Bola b: bolas){
                Vetor distancia = Bola.distancia(b, bolaBranca);
                if(distancia.norma<=Constantes.RAIO*2){
                    dentro=true;
                }
            }

            if(dentro==false)
                bolaBranca.caiu=false; 
            
        }
    }

    
   

}