import java.awt.Color;

public class Jogador {
    Color cor = Color.white;

    int pontos; //Quantidade de pontos do jogador
    String nome; //Nome do jogador

    Boolean vez; //Indica se é a vez do jogador(Lógica inversa)
    Boolean jogando; //Indica se o joga ainda está acontecendo

    //Construtor do jogador
    Jogador(){
        pontos = 0;
        jogando=true;
    }

    //Adiciona umponto ao jogador
    public void addPontos(){
        pontos++;
    }

    //Modifica a cor do jogador
    public void setColor(Color cor){
        this.cor=cor;
    }


}
