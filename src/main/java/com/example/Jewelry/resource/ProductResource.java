package com.example.Jewelry.resource;

import com.example.Jewelry.Utility.Constant;
import com.example.Jewelry.dao.CategoryDAO;
import com.example.Jewelry.dao.ProductDAO;
import com.example.Jewelry.dto.ProductDTO;
import com.example.Jewelry.dto.request.AddProductRequestDTO;
import com.example.Jewelry.dto.response.CommonApiResponse;
import com.example.Jewelry.dto.response.ImageDTO;
import com.example.Jewelry.dto.response.ProductResponseDTO;
import com.example.Jewelry.entity.Category;
import com.example.Jewelry.entity.Image;
import com.example.Jewelry.entity.Product;
import com.example.Jewelry.entity.User;
import com.example.Jewelry.exception.CategorySaveFailedException;
import com.example.Jewelry.service.CategoryService;
import com.example.Jewelry.service.ProductService;
import com.example.Jewelry.service.StorageService;
import com.example.Jewelry.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class ProductResource {
    private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Value("${com.lms.course.video.folder.path}")
    private String PRODUCT_BASEPATH;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    public ResponseEntity<ProductResponseDTO> addProduct(AddProductRequestDTO request) {

        LOG.info("received request for adding the product");

        ProductResponseDTO response = new ProductResponseDTO();

        if (request == null) {
            response.setResponseMessage("missing request body");
            response.setSuccess(false);
            return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        if (request.getCategoryId() == 0 || request.getDescription() == null
                || request.getName() == null) {
                    
            response.setResponseMessage("missing input " + Boolean.toString(request.getCategoryId() == 0) 
            + "::" + Boolean.toString(request.getDescription() == null)
             + "::" + Boolean.toString(request.getName() == null));
            response.setSuccess(false);
            return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        Category category = this.categoryService.getCategoryById(request.getCategoryId());

        if (category == null) {
            response.setResponseMessage("category not found");
            response.setSuccess(false);
            return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        User ctvOrAdmin = this.userService.getUserById(request.getCtvOrAdminId());

        if (ctvOrAdmin == null) {
            response.setResponseMessage("admin not found");
            response.setSuccess(false);
            return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        Product product = AddProductRequestDTO.toEntity(request);
        product.setProductIsBadge(request.getProductIsBadge());
        product.setDeleted(false);
        product.setCategory(category);
        product.setStatus(Constant.ActiveStatus.ACTIVE.value());

        // Upload ảnh và gán vào danh sách
        List<Image> images = new ArrayList<>();
        if (request.getImages() != null) {
            for (MultipartFile imageFile : request.getImages()) {
                if (!imageFile.isEmpty()) {
                    try {
                        // Ví dụ: upload ảnh và nhận lại URL (thay thế bằng logic thực tế của bạn)
                        String imageUrl = storageService.storeProductImage(imageFile); // cần tạo service

                        Image img = new Image();
                        img.setUrl(imageUrl);
                        img.setProduct(product); // liên kết ngược

                        images.add(img);
                    } catch (Exception e) {
                        LOG.error("Failed to upload image", e);
                    }
                }
            }
        }

        product.setImages(images);

        Product saveProduct = this.productService.add(product);

        if (saveProduct == null) {
            response.setResponseMessage("Failed to add the course");
            response.setSuccess(false);

            return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            response.setProduct(saveProduct);
            response.setResponseMessage("Product Created Successful, Add now....");
            response.setSuccess(true);

            return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.OK);
        }
    }

    public ResponseEntity<ProductResponseDTO> fetchAllProductByCategory(String categoryName) {
        ProductResponseDTO response = new ProductResponseDTO();
        if (categoryName == null) {
            response.setProducts(null);
            response.setResponseMessage("Category Name null");
            response.setSuccess(false);

            return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.BAD_REQUEST);
        }

        boolean categoryExists = categoryService.existsByName(categoryName);

        if (!categoryExists) {
            response.setProducts(null);
            response.setResponseMessage("Category ID Not Found");
            response.setSuccess(false);

            return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.NOT_FOUND);
        }

        List<Product> products = this.productService.getByCategoryNameAndStatus(categoryName, Constant.ActiveStatus.ACTIVE.value());

        response.setProducts(products);
        response.setResponseMessage("Fetch Product List By Category ID Successfully !");
        response.setSuccess(false);

        return new ResponseEntity<ProductResponseDTO>(response, HttpStatus.OK);
    }

    public ResponseEntity<ProductResponseDTO> fetchAllProduct() {
        List<Product> products = productDAO.findAll()
                .stream()
                .filter(product -> !product.isDeleted())
                .toList();

        List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .toList();

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setProductDTOs(productDTOs);
//        responseDTO.setProducts(products);
        responseDTO.setResponseMessage("Fetched all products successfully");

        return ResponseEntity.ok(responseDTO);
    }

    private ProductDTO convertToDTO(Product product) {
        List<ImageDTO> imageDTOs = new ArrayList<>();
        if (product.getImages() != null) {
            for (Image img : product.getImages()) {
                imageDTOs.add(new ImageDTO(img.getId(), img.getUrl()));
            }
        }

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .brand(product.getBrand())
                .imageURLs(imageDTOs) // sử dụng DTO ảnh
                .size(product.getSize())
                .productMaterial(product.getProductMaterial())
                .occasion(product.getOccasion())
                .prevPrice(product.getPrevPrice())
                .productIsFavorite(product.getProductIsFavorite())
                .productIsCart(product.getProductIsCart())
                .productIsBadge(product.getProductIsBadge())
                .deleted(product.isDeleted())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updateAt(product.getUpdateAt())
                .deletedAt(product.getDeletedAt())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : 0)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .averageRating(2)  //rating tam thoi
                .totalRating(2)
                .status(product.getStatus())
                .build();
    }


    public void fetchProductImage(String productImageName, HttpServletResponse resp) {
        Resource resource = storageService.loadProductImage(productImageName);
        if (resource == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType = URLConnection.guessContentTypeFromName(productImageName);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // Mặc định nếu không xác định được loại file
        }

        resp.setContentType(mimeType);

        try (InputStream in = resource.getInputStream();
             ServletOutputStream out = resp.getOutputStream()) {
            FileCopyUtils.copy(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity<CommonApiResponse> reStoreProduct(int productId) {
            LOG.info("Request received to restock product with ID: {}", productId);

            CommonApiResponse response = new CommonApiResponse();

            if (productId == 0) {
                response.setResponseMessage("Missing Product ID");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            Product product = productService.getById(productId);

            if (product == null) {
                response.setResponseMessage("Product not found");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            if (!product.isDeleted()) {
                response.setResponseMessage("Product is already active");
                response.setSuccess(false);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            product.setStatus(Constant.ActiveStatus.ACTIVE.value());
            product.setDeleted(false);
            product.setDeletedAt(null);
            product.setUpdateAt(LocalDateTime.now());

            Product updatedProduct = productService.update(product);

            if (updatedProduct == null) {
                throw new CategorySaveFailedException("Failed to restock the Product");
            }

            response.setResponseMessage("Product restocked successfully");
            response.setSuccess(true);
            return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<CommonApiResponse> deleteProduct(int productId) {
        LOG.info("Request received for deleting product");

        CommonApiResponse response = new CommonApiResponse();

        if (productId == 0) {
            response.setResponseMessage("missing Product Id");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
        }

        Product product = this.productService.getById(productId);

        if (product == null) {
            response.setResponseMessage("Product not found");
            response.setSuccess(false);

            return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        product.setStatus(Constant.ActiveStatus.DELETED.value());
        product.setDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        Product updateProduct = this.productService.update(product);

        if (updateProduct == null) {
            throw new CategorySaveFailedException("Failed to delete the Product");
        }

        response.setResponseMessage("Product Deleted Successful");
        response.setSuccess(true);

        return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
    }

    public ResponseEntity<CommonApiResponse> deleteProductPermanently(int productId) {
        LOG.info("Request received for permanently deleting product with ID: {}", productId);

        CommonApiResponse response = new CommonApiResponse();

        if (productId == 0) {
            response.setResponseMessage("Missing Product ID");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra xem category có tồn tại không
        Optional<Product> optionalProduct = productDAO.findById(productId);
        if (!optionalProduct.isPresent()) {
            response.setResponseMessage("Product not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            // Xóa category khỏi database
            productDAO.deleteById(productId);
            response.setResponseMessage("Product deleted permanently");
            response.setSuccess(true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error deleting Product: {}", e.getMessage());
            response.setResponseMessage("Failed to delete Product");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<CommonApiResponse> updateProduct(int productId, ProductDTO request) {
        LOG.info("Request received for update product");

        CommonApiResponse response = new CommonApiResponse();

        if (request == null) {
            response.setResponseMessage("Missing input");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (productId == 0) {
            response.setResponseMessage("Missing product ID");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra xem category có tồn tại không
        Product existingProduct = productService.getById(productId);
        if (existingProduct == null) {
            response.setResponseMessage("product not found");
            response.setSuccess(false);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Cập nhật thông tin product
        existingProduct = ProductDTO.toEntity(request);
        Category changeCategory = categoryDAO.findById(request.getCategoryId()).get();
        existingProduct.setId(productId);
        existingProduct.setCategory(changeCategory);

        // Upload ảnh và gán vào danh sách
        List<Image> images = existingProduct.getImages() != null ? new ArrayList<>(existingProduct.getImages()) : new ArrayList<>();

        if (request.getImages() != null) {
            for (MultipartFile imageFile : request.getImages()) {
                if (!imageFile.isEmpty()) {
                    try {
                        String imageUrl = storageService.storeProductImage(imageFile);
                        Image img = new Image();
                        img.setUrl(imageUrl);
                        img.setProduct(existingProduct);
                        images.add(img);
                    } catch (Exception e) {
                        LOG.error("Failed to upload image", e);
                    }
                }
            }
        }

        existingProduct.setImages(images);

        // Lưu product đã cập nhật
        Product savedProduct = productService.update(existingProduct);

        if (savedProduct == null) {
            throw new CategorySaveFailedException("Failed to update product");
        }

        response.setResponseMessage("product Updated Successfully");
        response.setSuccess(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ProductResponseDTO> getActiveProductList() {
        List<ProductDTO> productDTOList = productService.getActiveProductListForShop();
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setProductDTOs(productDTOList);
        responseDTO.setSuccess(true);
        responseDTO.setResponseMessage("Lấy danh sách sản phẩm trong shop thành công");
        return ResponseEntity.ok(responseDTO);
    }

    public ResponseEntity<ProductResponseDTO> getProductById(int product_id) {
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        Product product =  productService.getById(product_id);
        if (product == null) {
            responseDTO.setSuccess(false);
            responseDTO.setResponseMessage("Không tìm thấy sản phẩm mã " + product_id);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }

        ProductDTO productDTO = convertToDTO(product);
        responseDTO.setProductDTO(productDTO);
        responseDTO.setSuccess(true);
        responseDTO.setResponseMessage("Lấy danh sách sản phẩm trong shop thành công");
        return ResponseEntity.ok(responseDTO);
    }


}
