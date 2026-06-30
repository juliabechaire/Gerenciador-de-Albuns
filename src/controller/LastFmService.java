package controller;

import model.*;
import exception.ArquivoNaoEncontradoException;
import exception.DadosInvalidosException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class LastFmService {

    private static final String API_KEY  = "93bd1c2545d911220f27047b8ffaa5a8";
    private static final String BASE_URL = "http://ws.audioscrobbler.com/2.0/";

    
    public List<ResultadoBusca> buscarAlbum(String termo)
            throws ArquivoNaoEncontradoException, IOException, DadosInvalidosException {

        if (termo == null || termo.trim().isEmpty())
            throw new DadosInvalidosException("O termo de busca não pode ser vazio.");

        String encoded = URLEncoder.encode(termo.trim(), StandardCharsets.UTF_8);
        String url = BASE_URL + "?method=album.search&album=" + encoded
                   + "&api_key=" + API_KEY + "&format=json&limit=8";

        String resposta = fazerRequisicao(url);
        JSONObject json = new JSONObject(resposta);

        JSONArray matches;
        try {
            matches = json.getJSONObject("results")
                          .getJSONObject("albummatches")
                          .getJSONArray("album");
        } catch (Exception e) {
            throw new ArquivoNaoEncontradoException("Nenhum álbum encontrado para: " + termo);
        }

        if (matches.length() == 0)
            throw new ArquivoNaoEncontradoException("Nenhum álbum encontrado para: " + termo);

        List<ResultadoBusca> resultados = new ArrayList<>();
        for (int i = 0; i < matches.length(); i++) {
            JSONObject item = matches.getJSONObject(i);
            String nomeAlbum  = item.optString("name", "");
            String nomeArtist = item.optString("artist", "");
            String capa       = extrairMelhorCapa(item.optJSONArray("image"));
            resultados.add(new ResultadoBusca(nomeAlbum, nomeArtist, capa));
        }
        return resultados;
    }

    public void importarAlbumCompleto(Musica musica, String nomeAlbum, String nomeArtista)
            throws ArquivoNaoEncontradoException, IOException {

        String encAlbum   = URLEncoder.encode(nomeAlbum.trim(),   StandardCharsets.UTF_8);
        String encArtista = URLEncoder.encode(nomeArtista.trim(), StandardCharsets.UTF_8);
        String url = BASE_URL + "?method=album.getinfo&album=" + encAlbum
                   + "&artist=" + encArtista
                   + "&api_key=" + API_KEY + "&format=json";

        String resposta = fazerRequisicao(url);
        JSONObject json = new JSONObject(resposta);

        if (json.has("error"))
            throw new ArquivoNaoEncontradoException("Álbum não encontrado na base Last.fm.");

        JSONObject album = json.getJSONObject("album");

        musica.setNome(album.optString("name", nomeAlbum));
        musica.setArtista(album.optString("artist", nomeArtista));
        musica.setUrlCapa(extrairMelhorCapa(album.optJSONArray("image")));

        try {
            JSONArray tags = album.getJSONObject("tags").getJSONArray("tag");
            if (tags.length() > 0)
                musica.setGenero(tags.getJSONObject(0).optString("name", ""));
        } catch (Exception ignored) {}

        try {
            String published = album.getJSONObject("wiki").optString("published", "");
            if (!published.isEmpty()) {
                String ano = published.substring(published.lastIndexOf(" ") + 1).trim().replace(",", "");
                musica.setAnoLancamento(Integer.parseInt(ano));
            }
        } catch (Exception ignored) {}

        try {
            JSONArray tracks = album.getJSONObject("tracks").getJSONArray("track");
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject t = tracks.getJSONObject(i);
                int numero;
                try { numero = t.getJSONObject("@attr").optInt("rank", i + 1); }
                catch (Exception ig) { numero = i + 1; }
                String titulo = t.optString("name", "Faixa " + (i + 1));
                int duracao   = t.optInt("duration", 0);
                musica.addFaixa(new Faixa(numero, titulo, duracao));
            }
        } catch (Exception ignored) {}

        musica.importarDados();
    }

    
    public List<ResultadoBuscaFaixa> buscarFaixa(String termo)
            throws ArquivoNaoEncontradoException, IOException, DadosInvalidosException {

        if (termo == null || termo.trim().isEmpty())
            throw new DadosInvalidosException("O termo de busca não pode ser vazio.");

        String encoded = URLEncoder.encode(termo.trim(), StandardCharsets.UTF_8);
        String url = BASE_URL + "?method=track.search&track=" + encoded
                   + "&api_key=" + API_KEY + "&format=json&limit=8";

        String resposta = fazerRequisicao(url);
        JSONObject json = new JSONObject(resposta);

        JSONArray matches;
        try {
            matches = json.getJSONObject("results")
                          .getJSONObject("trackmatches")
                          .getJSONArray("track");
        } catch (Exception e) {
            throw new ArquivoNaoEncontradoException("Nenhuma faixa encontrada para: " + termo);
        }

        if (matches.length() == 0)
            throw new ArquivoNaoEncontradoException("Nenhuma faixa encontrada para: " + termo);

        List<ResultadoBuscaFaixa> resultados = new ArrayList<>();
        for (int i = 0; i < matches.length(); i++) {
            JSONObject item = matches.getJSONObject(i);
            String nomeFaixa  = item.optString("name", "");
            String nomeArtist = item.optString("artist", "");
            String capa       = extrairMelhorCapa(item.optJSONArray("image"));
            resultados.add(new ResultadoBuscaFaixa(nomeFaixa, nomeArtist, capa));
        }
        return resultados;
    }

    public void importarFaixaIndividual(Musica musica, String nomeFaixa, String nomeArtista)
            throws ArquivoNaoEncontradoException, IOException {

        String encFaixa   = URLEncoder.encode(nomeFaixa.trim(),   StandardCharsets.UTF_8);
        String encArtista = URLEncoder.encode(nomeArtista.trim(), StandardCharsets.UTF_8);
        String url = BASE_URL + "?method=track.getinfo&track=" + encFaixa
                   + "&artist=" + encArtista
                   + "&api_key=" + API_KEY + "&format=json";

        String resposta = fazerRequisicao(url);
        JSONObject json = new JSONObject(resposta);

        if (json.has("error"))
            throw new ArquivoNaoEncontradoException("Faixa não encontrada na base Last.fm.");

        JSONObject track = json.getJSONObject("track");

        musica.setNome(track.optString("name", nomeFaixa));
        musica.setArtista(track.optJSONObject("artist") != null
            ? track.getJSONObject("artist").optString("name", nomeArtista)
            : nomeArtista);

        JSONObject albumInfo = track.optJSONObject("album");
        if (albumInfo != null) {
            musica.setUrlCapa(extrairMelhorCapa(albumInfo.optJSONArray("image")));
        }

        int duracaoMs = track.optInt("duration", 0);
        int duracaoSeg = duracaoMs > 0 ? duracaoMs / 1000 : 0;

        try {
            JSONArray tags = track.getJSONObject("toptags").getJSONArray("tag");
            if (tags.length() > 0)
                musica.setGenero(tags.getJSONObject(0).optString("name", ""));
        } catch (Exception ignored) {}

        musica.addFaixa(new Faixa(1, musica.getNome(), duracaoSeg));

        musica.setUrlLink("https://www.last.fm/music/"
            + URLEncoder.encode(musica.getArtista(), StandardCharsets.UTF_8) + "/_/"
            + URLEncoder.encode(musica.getNome(), StandardCharsets.UTF_8));

        musica.importarDados();
    }

    private String fazerRequisicao(String urlStr) throws IOException {
        URL url = java.net.URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestProperty("User-Agent", "CofreCultural/1.0");

        if (conn.getResponseCode() != 200)
            throw new IOException("Erro HTTP: " + conn.getResponseCode());

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
        }
        return sb.toString();
    }


    public void buscarArtistaInfo(Artista artista, String nomeArtista)
            throws ArquivoNaoEncontradoException, IOException, DadosInvalidosException {

        if (nomeArtista == null || nomeArtista.trim().isEmpty())
            throw new DadosInvalidosException("O nome do artista não pode ser vazio.");

        String encoded = URLEncoder.encode(nomeArtista.trim(), StandardCharsets.UTF_8);
        String url = BASE_URL + "?method=artist.getinfo&artist=" + encoded
                   + "&api_key=" + API_KEY + "&format=json";

        String resposta = fazerRequisicao(url);
        JSONObject json = new JSONObject(resposta);

        if (json.has("error"))
            throw new ArquivoNaoEncontradoException("Artista não encontrado no Last.fm: " + nomeArtista);

        JSONObject artistJson = json.getJSONObject("artist");

        artista.setNome(artistJson.optString("name", nomeArtista));
        artista.setUrlFoto(extrairMelhorCapa(artistJson.optJSONArray("image")));

        try {
            JSONArray tags = artistJson.getJSONObject("tags").getJSONArray("tag");
            if (tags.length() > 0)
                artista.setGenero(tags.getJSONObject(0).optString("name", ""));
        } catch (Exception ignored) {}

        try {
            String bio = artistJson.getJSONObject("bio").optString("summary", "");
            bio = bio.replaceAll("<a href=.*?</a>", "").trim();
            if (!bio.isEmpty())
                artista.setDescricao(bio);
        } catch (Exception ignored) {}
    }

    private String extrairMelhorCapa(JSONArray images) {
        if (images == null) return "";
        String[] prioridade = {"mega", "extralarge", "large", "medium", "small"};
        for (String tamanho : prioridade) {
            for (int i = 0; i < images.length(); i++) {
                JSONObject img = images.optJSONObject(i);
                if (img != null && tamanho.equals(img.optString("size", ""))) {
                    String url = img.optString("#text", "");
                    if (!url.isEmpty()) return url;
                }
            }
        }
        return "";
    }

    public static class ResultadoBusca {
        public final String nome;
        public final String artista;
        public final String urlCapa;

        public ResultadoBusca(String nome, String artista, String urlCapa) {
            this.nome    = nome;
            this.artista = artista;
            this.urlCapa = urlCapa;
        }

        @Override
        public String toString() { return nome + " — " + artista; }
    }

    public static class ResultadoBuscaFaixa {
        public final String nome;
        public final String artista;
        public final String urlCapa;

        public ResultadoBuscaFaixa(String nome, String artista, String urlCapa) {
            this.nome    = nome;
            this.artista = artista;
            this.urlCapa = urlCapa;
        }

        @Override
        public String toString() { return nome + " — " + artista; }
    }
}