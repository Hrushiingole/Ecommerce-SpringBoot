package com.ecom.Shopping_Cart.service.ServiceImpl;

import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.repository.ProductRepository;
import com.ecom.Shopping_Cart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product saveProduct(Product product) {
        productRepository.save(product);
        return product;
    }

    @Override
    public List<Product> getAllProduct() {
        List<Product> products = productRepository.findAll();
        return  products;
    }

    @Override
    public Boolean deleteProduct(int id) {
        Product product=productRepository.findById(id).orElse(null);
        if(product!=null){
            productRepository.deleteById(id);
            return true;
        }
        else{
            return false;
        }

    }

    @Override
    public Product getProductById(int id) {
        Product product=productRepository.findById(id).orElse(null);
        return product;
    }

    @Override
    public Product updateProduct(Product product, @RequestParam("file") MultipartFile image) {
        Product oldProduct = productRepository.findById(product.getId()).orElse(null);
        oldProduct.setTitle(product.getTitle());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setStock(product.getStock());
        oldProduct.setDescription(product.getDescription());
        String imageName = image.isEmpty() ? oldProduct.getImage() : image.getOriginalFilename();
        oldProduct.setImage(imageName);
        oldProduct.setIsActive(product.getIsActive());
        oldProduct.setDiscount(product.getDiscount());
        Double discount = product.getPrice()*(product.getDiscount()/100.0);
        Double discountPrice = product.getPrice()-discount;
        oldProduct.setDiscountPrice(discountPrice);


        Product upadatedProduct = productRepository.save(oldProduct);
         if(upadatedProduct!=null){
            if(!image.isEmpty()){
                try {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + imageName);
                    System.out.println(path);
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return product;
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products=null;
        if(!category.isEmpty()){
            products=productRepository.findByCategory(category);
            return products;
        }
        if(category.isEmpty()){
           products =productRepository.findByIsActiveTrue();
        }

        return products;
    }

}
