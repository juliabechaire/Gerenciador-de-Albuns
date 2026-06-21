package controller;

import model.Livro;
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


public class OpenLibraryService {

    private static final String SEARCH_URL = "https://openlibrary.org/search.json";
    private static final String COVER_URL  = "https://covers.openlibrary.org/b/id/";

    public List<ResultadoBuscaLivro> buscarLivro(String termo)
            throws ArquivoNaoEncontradoException, IOException, DadosInvalidosException {

        if (termo == null || termo.trim().isEmpty())
            throw new DadosInvalidosException("O termo de busca não pode ser vazio.");

        String encoded = URLEncoder.encode(termo.trim(), StandardCharsets.UTF_8);
        String url = SEARCH_URL + "?q=" + encoded
                   + "&fields=key,title,author_name,first_publish_year,cover_i"
                   + "&limit=8";

        String resposta = fazerRequisicao(url);
        JSONObject json = new JSONObject(resposta);
        JSONArray docs = json.optJSONArray("docs");

        if (docs == null || docs.length() == 0)
            throw new ArquivoNaoEncontradoException("Nenhum livro encontrado para: " + termo);

        List<ResultadoBuscaLivro> resultados = new ArrayList<>();
        for (int i = 0; i < docs.length(); i++) {
            JSONObject item = docs.getJSONObject(i);
            String titulo = item.optString("title", "");

            String autor = "Desconhecido";
            JSONArray autores = item.optJSONArray("author_name");
            if (autores != null && autores.length() > 0) autor = autores.getString(0);

            int ano = item.optInt("first_publish_year", 0);
            int coverId = item.optInt("cover_i", 0);
            String capa = coverId > 0 ? COVER_URL + coverId + "-L.jpg" : "";

            String workKey = item.optString("key", ""); // ex: /works/OL12345W

            resultados.add(new ResultadoBuscaLivro(workKey, titulo, autor, ano, capa));
        }
        return resultados;
    }

    public void importarLivroCompleto(Livro livro, ResultadoBuscaLivro selecionado)
            throws ArquivoNaoEncontradoException, IOException {

        livro.setNome(selecionado.titulo);
        livro.setAutor(selecionado.autor);
        if (selecionado.ano > 0) livro.setAnoLancamento(selecionado.ano);
        if (!selecionado.capa.isEmpty()) livro.setImagem(selecionado.capa);

        if (!selecionado.workKey.isEmpty()) {
            try {
                String url = "https://openlibrary.org" + selecionado.workKey + ".json";
                String resposta = fazerRequisicao(url);
                JSONObject json = new JSONObject(resposta);

                JSONArray subjects = json.optJSONArray("subjects");
                if (subjects != null && subjects.length() > 0)
                    livro.setGenero(subjects.getString(0));

                livro.setLink("https://openlibrary.org" + selecionado.workKey);

            } catch (Exception ignored) {
                livro.setLink("https://openlibrary.org" + selecionado.workKey);
            }
        }
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

    public static class ResultadoBuscaLivro {
        public final String workKey;
        public final String titulo;
        public final String autor;
        public final int ano;
        public final String capa;

        public ResultadoBuscaLivro(String workKey, String titulo, String autor, int ano, String capa) {
            this.workKey = workKey;
            this.titulo  = titulo;
            this.autor   = autor;
            this.ano     = ano;
            this.capa    = capa;
        }

        @Override
        public String toString() {
            return titulo + " — " + autor + (ano > 0 ? " (" + ano + ")" : "");
        }
    }
}