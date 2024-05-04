package br.com.alura.tabelafipe.main;

import br.com.alura.tabelafipe.model.Data;
import br.com.alura.tabelafipe.model.Models;
import br.com.alura.tabelafipe.model.Vehicle;
import br.com.alura.tabelafipe.service.ConsumptionAPI;
import br.com.alura.tabelafipe.service.ConvertsData;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private Scanner scanner = new Scanner(System.in);
    private ConsumptionAPI consumptionAPI = new ConsumptionAPI();
    private ConvertsData converter = new ConvertsData();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";

    public void showMenu() {
        var menu = """
            *** OPÇÕES ***
            Carro
            Moto
            Caminhão
            
            Digite uma das opções para consulta:
            
            """;

        System.out.println(menu);
        var option = scanner.nextLine();

        String adress;

        if (option.toLowerCase().contains("carr")) {
            adress = URL_BASE + "carros/marcas";
        } else if (option.toLowerCase().contains("mot")) {
            adress = URL_BASE + "motos/marcas";
        } else {
            adress = URL_BASE + "caminhoes/marcas";
        }

        var json = consumptionAPI.getData(adress);
        System.out.println(json);


        var brands = converter.getList(json, Data.class);
        brands.stream()
                .sorted(Comparator.comparing(Data::code))
                .forEach(brand ->{
                    System.out.println("Código: " + brand.code());
                    System.out.println("Nome: " + brand.name());
                    System.out.println("---------------------");
                });

        System.out.println("Informe o código da marca para consulta");
        var codeBrand = scanner.nextLine();


        adress = adress + "/" + codeBrand + "/modelos";
        json = consumptionAPI.getData(adress);

        var modelList = converter.getData(json, Models.class);
        System.out.println("\nModelos dessa marca:");
        modelList.models().stream()
                .sorted(Comparator.comparing(Data::code))
                .forEach(model -> {
                    System.out.println("Código: " + model.code());
                    System.out.println("Modelo: " + model.name());
                    System.out.println("---------------------");
                });

        System.out.println("\nDigite um trecho do nome do carro a ser buscado:");
        var vehicleName = scanner.nextLine();

        List<Data> filteredModels = modelList.models().stream()
                        .filter(model -> model.name().toLowerCase().contains(vehicleName.toLowerCase()))
                                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados");
        filteredModels.forEach(model -> {
            System.out.println("Código: " + model.code());
            System.out.println("Modelo: " + model.name());
            System.out.println("---------------------");
        });



        System.out.println("Digite por favor o código do modelo para buscar os valores de avaliação");
        var codeModel = scanner.nextLine();

        adress = adress + "/" + codeModel + "/anos";
        json = consumptionAPI.getData(adress);

        List<Data> years = converter.getList(json, Data.class);

        List<Vehicle> vehicleList = new ArrayList<>();

        for (int i = 0; i < years.size(); i++) {
            var addressYears = adress + "/" + years.get(i).code();
            json = consumptionAPI.getData(addressYears);
            Vehicle vehicle = converter.getData(json, Vehicle.class);
            vehicleList.add(vehicle);
        }

        System.out.println("\nTodos os veiculos filtrados com avaliações por ano: ");
        vehicleList.forEach(vehicle -> {
            System.out.println("Modelo: " + vehicle.model());
            System.out.println("Marca: " + vehicle.brand());
            System.out.println("Valor: " + vehicle.value());
            System.out.println("Ano: " + vehicle.year());
            System.out.println("Tipo de Combustível: " + vehicle.typeFuel());
            System.out.println("---------------------");
        });


    }
}
