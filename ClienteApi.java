package api.jdtest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.*;

public class ClienteApi {

	public static void main(String[] args) throws Exception {

		System.out.println("Teste 1");
		System.out.println(buscar(1, 2));

		System.out.println();

		System.out.println("Teste 2");
		System.out.println(buscar(2, 1));

	}

	private static String buscar(int film_id, int c_id) throws Exception {

		// buscando filme
		JsonObject joFilme = getConteudo("https://swapi.co/api/films/" + film_id);

		// buscando personagem
		JsonObject joPersonagem = getConteudo("https://swapi.co/api/people/" + c_id + "/");
		// buscando especie do personagem (lista)
		JsonArray jArrayPersonagemEspecies = joPersonagem.getAsJsonArray("species");
		String urlEspecieDoPersonagem = jArrayPersonagemEspecies.get(0).getAsString();
		JsonObject jsonObjectEspecieDoPersonagem = getConteudo(urlEspecieDoPersonagem);
		String especieProcurada = jsonObjectEspecieDoPersonagem.get("name").getAsString();

		System.out.println(
				"Inicio da busca da especie [" + especieProcurada + "] no filme [" + joFilme.get("title") + "]");

		StringBuilder saida = new StringBuilder("[");

		// pegando um campos que Ã© uma lista
		JsonArray jsonArrayPersonagensFilme = joFilme.getAsJsonArray("characters");

		// Navegando por cada objeto da lista
		for (int i = 0; i < jsonArrayPersonagensFilme.size(); i++) {

			String urlPersonagem = jsonArrayPersonagensFilme.get(i).getAsString();

			JsonObject persongem = getConteudo(urlPersonagem);
			// apartir de um objeto json, pego o campo que eu desejar
			System.out.print("personagem: " + persongem.get("name"));

			// especies - pegando um campos que Ã© uma lista
			JsonArray jsonArrayEspeciesFilme = persongem.getAsJsonArray("species");

			for (int j = 0; j < jsonArrayEspeciesFilme.size(); j++) {
				String urlEspecies = jsonArrayEspeciesFilme.get(j).getAsString();
				JsonObject especie = getConteudo(urlEspecies);

				System.out.print(" especie: " + especie.get("name"));

				// buscando pela especie...
				if (especie.get("name").getAsString().equals(especieProcurada)) {

					System.out.println(" eh igual a especie [" + especieProcurada + "] !!!");
					saida.append(persongem.get("name") + ",");

				} else {

					System.out.println(" eh diferente da especie [" + especieProcurada + "]");

				}

			}

		}

		// apagando a ultima virgula
		saida.deleteCharAt(saida.length() - 1);
		saida.append("]");

		System.out.println("Final da busca...");

		return saida.toString();

	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private static JsonObject getConteudo(String url) throws Exception {

		// buscando a url
		HttpGet httpGet = new HttpGet(url);

		HttpClient httpClient = HttpClientBuilder.create().build();

		// indicando o tipo de retorno
		httpGet.addHeader("accept", "application/json");
		HttpResponse response = httpClient.execute(httpGet);

		// checando se nao houve falha
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Falha : HTTP error code : " + response.getStatusLine().getStatusCode());
		}

		// pegando o retorno e acumulando no stringBuilder
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}

		// Transformando o texto num objeto json pra navegar por ele
		Gson gson = new Gson();
		JsonObject jsonClass = gson.fromJson(stringBuilder.toString(), JsonObject.class);

		bufferedReader.close();

		// retornando um objeto json
		return jsonClass;

	}
}
