
import java.awt.*;
import java.util.*;



class Bola{
    Vetor posicao;
    Vetor velocidade;

    Boolean caiu;
    Boolean primeiraColisao = true;

    double raio;
    int id;

    Color cor;
    
    //Construtor de uma bola
    Bola(double xPos, double yPos, double xVel, double yVel, Color cor, int id, int raio){
        posicao = new Vetor(xPos, yPos);
        velocidade = new Vetor(xVel,yVel);
        this.raio = raio;
        this.id = id;
        this.cor = cor;
        caiu=false;
    }

    void setColor(Color cor){
        this.cor = cor;
    }

    //Aplica a força atrito em uma bola com base na constante ATRITO
    void atrito(){

        if(velocidade.x!=0){
            double dx = velocidade.diretor().x;

            //Se a velocidade em x for muito pequena é zerada para evitar bugs
            if(Math.abs(this.velocidade.x*dx)<Math.abs(Constantes.ATRITO*dx)) 
                this.velocidade.x=0;
            else
                this.velocidade.x-=Constantes.ATRITO*dx;
        }

        if(velocidade.y!=0){
            double dy = velocidade.diretor().y;

            //Se a velocidade em y for muito pequena é zerada para evitar bugs
            if(Math.abs(this.velocidade.y*dy)<Math.abs(Constantes.ATRITO*dy)) 
                this.velocidade.y=0;
            else
                this.velocidade.y-=Constantes.ATRITO*dy;
        }

        this.velocidade.atualiza();
    }

    //Calcula a distância entre duas bolas
    static Vetor distancia(Bola a, Bola b){
        Vetor distancia = Vetor.subtracao(b.posicao, a.posicao);
        return distancia;
    }

    //Checa se há colisão com alguma parede e se houver alguma calcula o que for necessário
    static void colideParedes(Bola b1,Insets insets){

        //Checagem de colisões com as paredes
        if(b1.posicao.x<=insets.left+b1.raio){
            b1.posicao.x+=insets.left+b1.raio-b1.posicao.x;
        }
        if(b1.posicao.y<=insets.top+b1.raio){
            b1.posicao.y+=insets.top+b1.raio-b1.posicao.y;
        }
        if(b1.posicao.x>=insets.right-b1.raio){
            b1.posicao.x+=insets.right-b1.raio-b1.posicao.x;
        }
        if(b1.posicao.y>=insets.bottom-b1.raio){
            b1.posicao.y+=insets.bottom-b1.raio-b1.posicao.y;
        }

        //Correção de sobreposição com as paredes
        if(b1.posicao.x-b1.raio<=insets.left || b1.posicao.x+b1.raio>=insets.right){
            b1.velocidade.x*=-1;
            if(b1.posicao.x-b1.raio>insets.right) 
                b1.posicao.x+=b1.posicao.x-b1.raio-insets.right+1;
        }
        if(b1.posicao.y-b1.raio<=insets.top || b1.posicao.y+b1.raio>=insets.bottom){
            b1.velocidade.y*=-1;
            if(b1.posicao.y-b1.raio>insets.bottom) 
                b1.posicao.y+=b1.posicao.y-b1.raio-insets.bottom+1;
        }

    }

    //Verificação de colisões entre bolas
    static void checaColisao(Bola bolaBranca, LinkedList<Bola> bolas, Insets insets){

        //Verifica todo o vetor de bolas
        for(Bola b1 : bolas){ 

                b1.atrito(); //Aplica atrito
    
                Bola.colideParedes(b1, insets); //Verifica colisão da bola com as paredes

                for(Bola b2 : bolas){
                    //Se houver intersecção entre a área de duas bolas é calculada a colisão entre elas
                    if(b2.id>b1.id){
                        Vetor distancia = Bola.distancia(b1, b2);
                        if(b1.id!=b2.id && distancia.norma<=Constantes.RAIO*2){
                            Bola.calculaColisao(b1, b2);
                        }
                    }
                }
                
                //Verifica cosisão com a bola branca
                Vetor distancia = Bola.distancia(b1, bolaBranca);
                if(bolaBranca.caiu==false && distancia.norma<=Constantes.RAIO*2){
                    Bola.calculaColisao(b1, bolaBranca);
                }

                //Movimentação da bola
                b1.posicao = Vetor.soma(b1.posicao, b1.velocidade); 
            }

            bolaBranca.atrito(); //Aplica atrito
            Bola.colideParedes(bolaBranca, insets); //Verifica colisão da bola branca com as paredes
            bolaBranca.posicao = Vetor.soma(bolaBranca.posicao, bolaBranca.velocidade); //Movimentação da bola

    }
    
    //Realiza o cálculo de colisão entre duas bolas
    static void calculaColisao(Bola a, Bola b){
        Vetor distancia = Bola.distancia(a,b);
        Vetor diretor = distancia.diretor();
        
        //Corrige a posição das bolas em caso de sobreposição de bolas
        double correcao = (a.raio*2-distancia.norma)/2;
        a.posicao = Vetor.soma(a.posicao, Vetor.multiplicacao(diretor, -correcao));
        b.posicao = Vetor.soma(b.posicao, Vetor.multiplicacao(diretor, correcao));
        
        //Calcula a velocidade absoluta das bolas
        double Va = Vetor.produtoEscalar(a.velocidade, diretor);
        double Vb = Vetor.produtoEscalar(b.velocidade, diretor);

        //Calcula a velocidade das bolas após a colisão
        double novoVa = (Va + Vb -(Va - Vb)*Constantes.RESTITUICAO)/2;
        double novoVb = (Va + Vb -(Vb - Va)*Constantes.RESTITUICAO)/2;

        //Atribui as novas velocidades as bolas
        a.velocidade = Vetor.soma(a.velocidade, Vetor.multiplicacao(diretor, novoVa-Va));
        b.velocidade = Vetor.soma(b.velocidade, Vetor.multiplicacao(diretor, novoVb-Vb));
    }

    //Função que desenha a bola
    public void paint(Graphics g){
		g.setColor(cor);
        g.fillOval((int)(Math.round(this.posicao.x))-(int)(raio),(int)(Math.round(this.posicao.y))-(int)(raio),2*(int)(raio),2*(int)(raio));
	}

}