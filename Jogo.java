import java.awt.*;
import javax.swing.event.*;
import javax.swing.*;

public class Jogo extends JFrame implements ChangeListener, Runnable{
    
    //Cria as variaveis e objetos do jogo
    Mesa mesa;
    
    Jogador jogador1,jogador2;
    
    JSlider slider = new JSlider(0,100,0);

    JLabel vez = new JLabel();
    JLabel forca = new JLabel();
    JLabel placar = new JLabel();

    JPanel dir = new JPanel(new FlowLayout());
    JPanel esq = new JPanel(new FlowLayout());
    JPanel mid = new JPanel(new BorderLayout());
    JPanel proSlider = new JPanel(new FlowLayout());
    JPanel praForca = new JPanel(new FlowLayout());


    //Altera valor da barra de força
    public void stateChanged(ChangeEvent e){
        forca.setText("Forca = " + slider.getValue());
    }
    

    //Construtor do Jogo
    Jogo(){

        //Cria e associa a fonte das letras dos Labels
        Font fonteVez_Forca = new Font( "Arial", Font.PLAIN, 20);  //Fonte da letra do indicador de vez e da força
        vez.setFont(fonteVez_Forca);
        forca.setFont(fonteVez_Forca);

        Font fontePlacar = new Font( "Arial", Font.PLAIN, 30);  //Fonte da letra do placar de pontos
        placar.setFont(fontePlacar);   
        placar.setForeground(Color.white);
        

        //Instancia jogadores
        jogador1 = new Jogador();
        jogador2 = new Jogador();


        //Adiciona o nome aos jogadores
        jogador1.nome = JOptionPane.showInputDialog(null,"Coloque o nome do Jogador 1:");
        jogador2.nome = JOptionPane.showInputDialog("Coloque o nome do Jogador 2:");
        

        //Controle de quem começa jogando
        Object[] options = { jogador1.nome, jogador2.nome}; //cria as opcoes pro OptionPane

        int i = JOptionPane.showOptionDialog(null, "Escolha quem comeca", null, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,null, options, options[0]);
        
        if (i==0){  //se jogador 1 começa, é sua vez
            jogador1.vez=true;
            jogador2.vez=false;
        }
        
        else{   //se jogador 2 começa, é sua vez
            jogador1.vez=false;
            jogador2.vez=true;
        }
        
        //Configura o Frame do Jogo
        setLayout(new BorderLayout());
        setSize(1015,700);
        setVisible(true);
        setLocationRelativeTo(null); 
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        
        //Adiciona Change Listener para o slider, para alterar a força ao deslizar
        slider.addChangeListener(this);
        
        
        //Instancia o a mesa (panel) e envia os objetos necessarios
        mesa = new Mesa(slider, jogador1, jogador2, vez, placar);
        mesa.atualizaVez(); //atualiza a vez do primeiro jogador no label da vez
        

        //Adiciona texto ao labels de força e placar
        forca.setText("Forca = " + slider.getValue());
        forca.setForeground(Color.white);
        placar.setText(jogador1.nome + " " + jogador1.pontos + " x " + jogador2.pontos + " " + jogador2.nome);
        

        //Adiciona cor ao fundo dos paneis e do slider
        Color corFundo = new Color(51, 18, 0);
        slider.setBackground(corFundo);
        esq.setBackground(corFundo);
        dir.setBackground(corFundo);
        proSlider.setBackground(corFundo);
        praForca.setBackground(corFundo);


        //Adiciona os panels a tela de forma organizada
        proSlider.add(slider);  //adiciona Slider ao panel pro slider
        mid.add(proSlider, BorderLayout.NORTH); //adiciona o panel pro slider no panel do meio (em cima)
        praForca.add(forca);    //adiciona o Label da força ao panel pra força
        mid.add(praForca, BorderLayout.CENTER); //adiciona o panel pra força no panel do meio (em baixo)
        
        esq.add(vez);     //adiciona o Label de vez ao panel da esquerda
        dir.add(placar);  //adiciona o Label do placar ao panel da direita
        
        add(mesa, BorderLayout.NORTH); //adiciona o panel da mesa em cima da tela
        add(esq,BorderLayout.WEST);  //adiciona o panel da esquerda na esquerda da tela
        add(mid,BorderLayout.CENTER); //adiciona o panel do meio no meio da tela
        add(dir,BorderLayout.EAST);  //adiciona o panel da direita na direita da tela
        

        //Inicia Thread para checar se o jogo acaba ou continua
        new Thread(this).start();

    }

    //Thread para checagem de jogo continuando
    public void run(){
        
        while(true){ //loop infinito até que o jogo acabe

            //Sleep para nao ter conflito
            try{
                Thread.sleep(10); 
            }catch(Exception e){};

            //Controle se o jogo acabou
            if(jogador1.jogando==false){ 
                this.dispose(); //fecha o jogo 
                new Menu();  //volta ao menu
                break;
            }
        }

    }

    
    


}
