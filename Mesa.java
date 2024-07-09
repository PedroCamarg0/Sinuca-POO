import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;

public class Mesa extends JPanel implements Runnable{

    //Cria variaveis e objetos
    LinkedList<Bola> bolas; //cria lista de Bolas
    LinkedList<Bola> cacapas; //cria listas de Cacapas do tipo Bola também
    Bola bolaBranca;  //cria a Bola Branca

    BufferedImage[] buffer = new BufferedImage[Constantes.BUFFERSIZE]; //cria buffer de imagem

    Insets insets = new Insets(42,38,517,961); //cria os limites da mesa
    int xPonta = (int)((insets.right-insets.left)*3/4+insets.left); //coordenadas da primeira bola colorida, no triangulo
    int yPonta = (int)((insets.bottom-insets.top)*1/2+insets.top);

   

    ImageIcon img = new ImageIcon(getClass().getResource("Mesa720.png")); // cria a imagem da mesa
    JLabel mesa = new JLabel(img);

    meuMouse mouse; //cria o mouse listener

    double energia = 0; //variavel energia do sistema na mesa
    
    Random rand = new Random(); //cria variavel para gerar numero aleatorio

    Jogador jogador1,jogador2; //cria objetos originarios do classe Jogo
    JLabel vez, placar;

    Boolean trocou = true; //cria as variaveis Booleanas para controle das regras e jogabilidade
    Boolean primeiraQueda = true;
    Boolean bateuErrado = false;
    Boolean derrubou = false;
    Boolean temVermelho, temAmarelo;
    

    //Construtor da mesa
    Mesa(JSlider slider, Jogador jogador1, Jogador jogador2, JLabel vez, JLabel placar){

        //Recebe os objetos do Jogo
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
        this.vez = vez;
        this.placar = placar;
        
        
        //Atribui Layout ao panel mesa
        setLayout(new FlowLayout());
        setSize( 1015, 590);
        setLocation(0,0);
        setVisible(true);
        setBackground(new Color(51, 18, 0));
        

        //Adiciona a imagem da mesa ao panel da mesa
        add(mesa);


        //Instancia a lista de bolas e a bola branca
        bolas = new LinkedList<Bola>();
        bolaBranca = new Bola(xPonta-(insets.right-insets.left)/2,yPonta, 0,0, Color.white, -1, Constantes.RAIO);


        //Posiciona as bolas coloridas
        posicionaBolas(xPonta,yPonta);
        

        //Instancia a lista de cacapas do tipo Bola e as posiciona na tela
        cacapas = new LinkedList<Bola>();
        cacapas.add(new Bola(44, 44, 0, 0, Color.white, 0, 35));
        cacapas.add(new Bola(505, 26, 0, 0, Color.white, 0, 30));
        cacapas.add(new Bola(966, 44, 0, 0, Color.white, 0, 35));
        cacapas.add(new Bola(966, 516, 0, 0, Color.white, 0, 35));
        cacapas.add(new Bola(505, 533, 0, 0, Color.white, 0, 30));
        cacapas.add(new Bola(44, 516, 0, 0, Color.white, 0, 35));
        
        
        //Instancia o mouse e adiciona os listeners atribuidos
        mouse = new meuMouse(bolaBranca, slider, bolas);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        //Pinta as bolas na tela
        repaint();

        //Instancia o buffer de imagem
        for (int i=0; i<Constantes.BUFFERSIZE; i++){
            buffer[i] = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        

        //Inicia a thread que roda o jogo
        new Thread(this).start();

        
    }





    //Funcao para pintar as bolas
    public void paint(Graphics g) {

        //Pinta os frames no buffer das bolas em jogo
        for(int i=0; i<Constantes.BUFFERSIZE; i++){
            Graphics g1 = buffer[i].getGraphics();
            super.paint(g1);
            bolaBranca.paint(g1);
            for(Bola b : bolas){
                b.paint(g1);
            }
            
        }

        super.paint(g);


        //Pinta a bola branca na tela
        bolaBranca.paint(g);


        //Pinta as bolas coloridas na tela
        for(Bola b : bolas){
            b.paint(g);
        }
       
    }



    //Thread que roda o jogo
    public void run(){

        //Laço que roda até o jogo acabar
        while(true){

            //Reseta o contador de energia do sistema
            energia = 0;
            energia+=bolaBranca.velocidade.norma;


            //Reseta a verificação se tem bolas coloridas na mesa, para controle se a rodada acabou
            temVermelho = false;
            temAmarelo = false;
        
            //Cria lista de bolas que devem ser removidas (controla Exceções)
            ArrayList<Bola> toRemove = new ArrayList<Bola>();

            //Loop para verificar se alguma bola caiu na caçapa
            for(Bola b1 : bolas){

                energia+=b1.velocidade.norma;

                for(Bola cacapa: cacapas){
                    if(Bola.distancia(cacapa, b1).norma < cacapa.raio){ //se a distnacia da bola e alguma caçapa for menor que o raio da caçapa, bola caiu
                        
                        toRemove.add(b1); //adiciona bola as bolas a serem removidas


                        //Controle da primeira queda, para atribuição das cores dos jogadores
                        if(primeiraQueda){ 

                            atribuiCor(b1); //atribui cor ao jogador respectivo a primeira bola derrubada e a vez

                            //Notifica o jogador de qual cor são suas bolas
                            JOptionPane.showMessageDialog(null, "As bolas de " + (jogador1.vez==false ? jogador1.nome : jogador2.nome) + " sao " + (b1.cor==Color.red ? "vermelhas":"amarelas"));

                            primeiraQueda = false;
                        }


                        //Controle se o jogador derrubou uma bola de sua respectiva cor em sua vez, para manter a vez na proxima jogada.
                        if(jogador1.vez == false && b1.cor == jogador1.cor)
                            derrubou = true;

                        else if(jogador2.vez == false && b1.cor == jogador2.cor)
                            derrubou = true;

                    }

                    
                }

                //Checagem de primeira colisao da bola branca com outra bola colorida no inicio de cada tacada
                if(bolaBranca.primeiraColisao == true && Bola.distancia(bolaBranca, b1).norma < 2*Constantes.RAIO){ //se a bola branca ainda nao colidiu e ela colidiu com outra, entra


                    //Controle se a primeira bola atingida pelo jogador é de sua cor, para penalidade de passagem da rodada, independente se derruba bola de sua respectiva cor após isso
                    if(primeiraQueda==false){   //se ja houve a atribuiçao de cores aos jogadores, realiza o controle
                        
                        if(jogador1.vez == false && b1.cor != jogador1.cor){ //se for a vez do jogador e a primeira bola atingida não for de sua cor, bateu errado 
                            bateuErrado = true;
                        }

                        if(jogador2.vez == false && b1.cor != jogador2.cor){
                            bateuErrado = true;
                        }
                    }
                    
                    bolaBranca.primeiraColisao = false; //primeira colisão ja foi
                }


                //Checagem se ainda há bolas de cores diferentes
                if(b1.cor==Color.yellow)
                    temAmarelo=true;
                    else if(b1.cor==Color.red)
                    temVermelho=true;

            }


            //Remove as bolas que estavam na lista para serem removidas
            bolas.removeAll(toRemove);


            //Controle se a rodada deve acabar
            if(temAmarelo==false || temVermelho==false){ // se apenas houver uma cor de bola na mesa, o jogo deve acabar
                if(acabou()==1){ // função que apresenta a escolha ao jogador de continuar jogando ou não
                    break; //caso nao queira, o jogo acaba
                }
            }


            //Checa se a bola branca caiu em alguma caçapa
            for(Bola cacapa: cacapas){
                if(Bola.distancia(cacapa, bolaBranca).norma < cacapa.raio){   
                    bolaBranca.caiu=true;
                    bolaBranca.velocidade = new Vetor(0,0);
                    bolaBranca.posicao=new Vetor(-1000, -1000); 
                }
            }
            
            
            //Checa colisões entre todas as bolas, inclusive a branca
            Bola.checaColisao(bolaBranca, bolas, insets);


            //Atribui a energia atual ao mouse, para controle
            mouse.setEnergia(energia);


            //Controle de troca de vez
            if(energia!=0) // espera a primeira mexida de bolas para realizar a troca de vez (serve apenas no inicio, quando tudo esta parado)
                trocou=false;

            
            if(energia==0 && trocou==false){  //se as bolas pararam deve realizar a checagem se troca a vez
                if(bateuErrado==true || derrubou==false) //se o jogador bateu primeiro na bola errada ou não derrubou sua bola, atualiza a vez
                    atualizaVez();

                //Reseta as variaveis
                derrubou = false; 
                bateuErrado = false;
                trocou = true;

                //Controle de penalidade caso a bola branca não encoste em nenhuma bola
                if(bolaBranca.primeiraColisao==true) // se não encostar, o outro jogador pode alterar a posição da bola branca
                    bolaBranca.caiu = true;
                bolaBranca.primeiraColisao=true; //reseta primeira colisao da bola branca

            }


            //Sleep da thread para não ser instantâneo 
            try{
                Thread.sleep(2);
            }catch(Exception e){}

            //Repinta as bolas
            repaint();

        }

    }

    
    //Funcao que posiciona as bolas na tela, no inicio do jogo
    final void posicionaBolas(int xPonta, int yPonta){
        

        int dy = (int)Constantes.RAIO+1;
        int dx = (int)((Constantes.RAIO*2+2)*0.87);
        

        bolas.add(new Bola(xPonta+dx, yPonta-dy, 0, 0, Color.red, 1, Constantes.RAIO));
        bolas.add(new Bola(xPonta+dx, yPonta+dy, 0, 0, Color.red, 2, Constantes.RAIO));

        bolas.add(new Bola(xPonta+2*dx, yPonta-2*dy, 0, 0, Color.red, 3, Constantes.RAIO));
        bolas.add(new Bola(xPonta+2*dx, yPonta, 0, 0, Color.red, 4, Constantes.RAIO));
        bolas.add(new Bola(xPonta+2*dx, yPonta+2*dy, 0, 0, Color.red, 5, Constantes.RAIO));

        bolas.add(new Bola(xPonta+3*dx, yPonta-3*dy, 0, 0, Color.red, 6, Constantes.RAIO));
        bolas.add(new Bola(xPonta+3*dx, yPonta-dy, 0, 0, Color.red, 7, Constantes.RAIO));
        bolas.add(new Bola(xPonta+3*dx, yPonta+dy, 0, 0, Color.red, 8, Constantes.RAIO));
        bolas.add(new Bola(xPonta+3*dx, yPonta+3*dy, 0, 0, Color.red, 9, Constantes.RAIO));

        bolas.add(new Bola(xPonta+4*dx, yPonta-4*dy, 0, 0, Color.red, 10, Constantes.RAIO));
        bolas.add(new Bola(xPonta+4*dx, yPonta-2*dy, 0, 0, Color.red, 11, Constantes.RAIO));
        bolas.add(new Bola(xPonta+4*dx, yPonta, 0, 0, Color.red, 12, Constantes.RAIO));
        bolas.add(new Bola(xPonta+4*dx, yPonta+2*dy, 0, 0, Color.red, 13, Constantes.RAIO));
        bolas.add(new Bola(xPonta+4*dx, yPonta+4*dy, 0, 0, Color.red, 14, Constantes.RAIO));

        bolaBranca.posicao = new Vetor(xPonta-(insets.right-insets.left)/2,yPonta);
        bolaBranca.velocidade = new Vetor(0,0);

        //Colore as bolas
        coloreBolas();
    }


    //Funcao que colore as bolas, aleatoriamente e de maneira igualitaria 7 bolas para cada
    final void coloreBolas(){
        int vermelhas = 0;
        int amarelas = 0;
        int qtd = 14;

        for(Bola b : bolas){
            int num = rand.nextInt(2);
            if((num==0 && vermelhas<qtd/2) || amarelas==qtd/2){
                b.setColor(Color.red);
                vermelhas++;
            }
            else if((num==1 && amarelas<qtd/2) || vermelhas==qtd/2){
                b.setColor(Color.yellow);
                amarelas++;
            }
        }
    }

    
    //Função para alterar o JLabel da vez
    public void atualizaVez(){

        if(jogador1.vez==true){
            vez.setText("       Vez de "+ jogador1.nome);
            vez.setForeground(jogador1.cor);
            jogador1.vez=false;//ja altera a vez do proximo, assim que altera o Label
            jogador2.vez=true;
        }
        else{
            vez.setText("       Vez de "+jogador2.nome);
            vez.setForeground(jogador2.cor);
            jogador1.vez=true;
            jogador2.vez=false;
        }

        
    }

    //Funcao para quando acabar a rodada
    public int acabou(){

        //Controle para saber qual jogador ganhou, 1 ou 2
        int ganhador;
        if(temAmarelo==false){
            if(jogador1.cor == Color.yellow)
                ganhador = 1;
            else
                ganhador = 2;
        }
        else{
            if(jogador1.cor == Color.red)
                ganhador = 1;
            else
                ganhador = 2;
        }


        //JOptionPane para usuário escolher se deseja continuar jogando ou não
        Object[] options = {"Sim", "Nao"};
        int index = JOptionPane.showOptionDialog(null, (ganhador==1 ? jogador1.nome : jogador2.nome) + " ganhou a partida!!\nDeseja continuar jogando?", "FIM DE JOGO!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,options,options[0]);
        
        if(index==1){ //se não deseja continuar jogando, fechará a janela do jogo, na classe Jogo
            jogador1.jogando=false;
            jogador2.jogando=false;
        }
        else{ //se sim, adiciona 1 ponto ao jogador ganhador
            if(ganhador==1)
                jogador1.pontos++;
            else
                jogador2.pontos++;
            
            //Reseta as variáveis e posições das bolas
            reseta();
        }
        return index;
    }
    

    //Função que reseta as variáveis e bolas para a rodada
    public void reseta(){
        
        //Altera o Label do placar
        placar.setText(jogador1.nome + " " + jogador1.pontos + " x " + jogador2.pontos + " " + jogador2.nome);


        //Reseta as variaveis
        jogador1.cor=Color.white;
        jogador2.cor=Color.white;
        trocou=true;
        primeiraQueda = true;
        bateuErrado = false;
        derrubou = false;
        bolas.clear();

        //Reseta a posicão das bolas
        posicionaBolas(xPonta,yPonta);

    }


    //Atribui a cor dos jogadores relativas a primeira bola derrubada
    public void atribuiCor(Bola b1){
        if(jogador1.vez == false){
            jogador1.setColor(b1.cor);
            vez.setForeground(jogador1.cor);
            if(b1.cor==Color.red)
                jogador2.setColor(Color.yellow);
            else
                jogador2.setColor(Color.red);
        }
        else{
            jogador2.setColor(b1.cor);
            vez.setForeground(jogador2.cor);
            if(b1.cor==Color.red)
                jogador1.setColor(Color.yellow);
            else
                jogador1.setColor(Color.red);
        }
    }

}




