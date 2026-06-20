package controller;

import model.Filme;
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

/**
 * Serviço de integração com a API do TMDB (The Movie Database).
 * Busca filmes, pôster, elenco, diretor, sinopse e gênero.
 * Exceções lançadas aqui (camada service) e tratadas na view.
 */
public class TMDBService {

    private static final String API_KEY  = "57c1052c29bc59b009da268c1f53f5c4";
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMG_BASE = "https://image.tmdb.org/t/p/w500";

    // ── Busca filmes pelo nome (retorna lista de candidatos) ─────────────
    public List<ResultadoBuscaFilme> buscarFilme(String termo)
            throws ArquivoNaoEncontradoException, IOException, DadosInvalidosException {

        if (termo == null || termo.trim().isEmpty())
            throw new DadosInvalidosException("O termo de busca não pode ser vazio.");

        String encoded = URLEncoder.encode(termo.trim(), StandardCharsets.UTF_8);
        String url = BASE_URL + "/search/movie?api_key=" + API_KEY
                   + "&query=" + encoded + "&language=pt-BR";

        String resposta = fazerRequisicao(url);
        JSONObject json = new JSONObject(resposta);
        JSONArray results = json.optJSONArray("results");

        if (results == null || results.length() == 0)
            throw new ArquivoNaoEncontradoException("Nenhum filme encontrado para: " + termo);

        List<ResultadoBuscaFilme> resultados = new ArrayList<>();
        for (int i = 0; i < Math.min(results.length(), 8); i++) {
            JSONObject item = results.getJSONObject(i);
            String titulo = item.optString("title", "");
            String dataLanc = item.optString("release_date", "");
            String ano = dataLanc.length() >= 4 ? dataLanc.substring(0, 4) : "";
            String posterPath = item.optString("poster_path", "");
            String poster = posterPath.isEmpty() ? "" : IMG_BASE + posterPath;
            int id = item.optInt("id", 0);
            resultados.add(new ResultadoBuscaFilme(id, titulo, ano, poster));
        }
        return resultados;
    }

    // ── Importa detalhes completos de um filme para o objeto Filme ───────
    public void importarFilmeCompleto(Filme filme, int tmdbId)
            throws ArquivoNaoEncontradoException, IOException {

        String url = BASE_URL + "/movie/" + tmdbId
                   + "?api_key=" + API_KEY + "&language=pt-BR&append_to_response=credits";

        String resposta = fazerRequisicao(url);
        JSONObject json = new JSONObject(resposta);

        if (json.has("status_code"))
            throw new ArquivoNaoEncontradoException("Filme não encontrado na base TMDB.");

        filme.setNome(json.optString("title", filme.getNome()));

        String posterPath = json.optString("poster_path", "");
        if (!posterPath.isEmpty()) filme.setImagem(IMG_BASE + posterPath);

        String dataLanc = json.optString("release_date", "");
        if (dataLanc.length() >= 4) {
            try { filme.setAnoLancamento(Integer.parseInt(dataLanc.substring(0, 4))); }
            catch (NumberFormatException ignored) {}
        }

        filme.setDuracao(json.optInt("runtime", 0));

        // Gênero — pega o primeiro da lista
        JSONArray generos = json.optJSONArray("genres");
        if (generos != null && generos.length() > 0)
            filme.setGenero(generos.getJSONObject(0).optString("name", ""));

        // Sinopse como link de referência (TMDB não tem link de "assistir" público)
        filme.setLink("https://www.themoviedb.org/movie/" + tmdbId);

        // Diretor e elenco via credits
        try {
            JSONObject credits = json.getJSONObject("credits");
            JSONArray crew = credits.optJSONArray("crew");
            if (crew != null) {
                for (int i = 0; i < crew.length(); i++) {
                    JSONObject pessoa = crew.getJSONObject(i);
                    if ("Director".equals(pessoa.optString("job", ""))) {
                        filme.setDiretor(pessoa.optString("name", "Desconhecido"));
                        break;
                    }
                }
            }
            JSONArray cast = credits.optJSONArray("cast");
            if (cast != null && cast.length() > 0) {
                StringBuilder elenco = new StringBuilder();
                int limite = Math.min(cast.length(), 4);
                for (int i = 0; i < limite; i++) {
                    if (i > 0) elenco.append(", ");
                    elenco.append(cast.getJSONObject(i).optString("name", ""));
                }
                filme.setElencoPrincipal(elenco.toString());
            }
        } catch (Exception ignored) {}
    }

    // ── Requisição HTTP simples ─────────────────────────────────────────
    private String fazerRequisicao(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        conn.setRequestProperty("User-Agent", "CofreCultural/1.0");

        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 404)
            throw new IOException("Erro HTTP: " + conn.getResponseCode());

        java.io.InputStream is = conn.getResponseCode() == 200
            ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
        }
        return sb.toString();
    }

    // ── Classe interna: resultado simplificado da busca ─────────────────
    public static class ResultadoBuscaFilme {
        public final int id;
        public final String titulo;
        public final String ano;
        public final String poster;

        public ResultadoBuscaFilme(int id, String titulo, String ano, String poster) {
            this.id      = id;
            this.titulo  = titulo;
            this.ano     = ano;
            this.poster  = poster;
        }

        @Override
        public String toString() {
            return titulo + (ano.isEmpty() ? "" : " (" + ano + ")");
        }
    }
}