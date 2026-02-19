//package tech.josef.config;
//
//
//import io.quarkus.runtime.StartupEvent;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.enterprise.event.Observes;
//import jakarta.transaction.Transactional;
//import tech.josef.entity.Product;
//import tech.josef.entity.ProductMaterial;
//import tech.josef.entity.RawMaterial;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Random;
//
//@ApplicationScoped
//public class DatabaseSeeder {
//
//    private final Random random = new Random();
//
//    @Transactional
//    void onStart(@Observes StartupEvent ev) {
//
//        if (Product.count() > 0) {
//            return;
//        }
//
//        // 🔹 1. Create 100 Raw Materials
//        for (int i = 1; i <= 100; i++) {
//            RawMaterial rawMaterial = RawMaterial.builder()
//                    .code("RM" + i)
//                    .name("Raw Material " + i)
//                    .stockQuantity(random.nextInt(200) + 50) // estoque 50-250
//                    .build();
//
//            rawMaterial.persist();
//        }
//
//        List<RawMaterial> allMaterials = RawMaterial.listAll();
//
//        // 🔹 2. Create 30 Products
//        for (int i = 1; i <= 30; i++) {
//
//            Product product = Product.builder()
//                    .code("PR" + i)
//                    .name("Product " + i)
//                    .price(BigDecimal.valueOf(random.nextInt(900) + 100)) // 100-1000
//                    .build();
//
//            product.persist();
//
//            // 🔹 3. Associate 3–5 random materials
//            int materialsCount = random.nextInt(3) + 3;
//
//            for (int j = 0; j < materialsCount; j++) {
//
//                RawMaterial rawMaterial =
//                        allMaterials.get(random.nextInt(allMaterials.size()));
//
//                ProductMaterial pm = ProductMaterial.builder()
//                        .product(product)
//                        .rawMaterial(rawMaterial)
//                        .requiredQuantity(random.nextInt(10) + 1) // 1-10
//                        .build();
//
//                pm.persist();
//            }
//        }
//
//        System.out.println("Database seeded successfully!");
//    }
//}
