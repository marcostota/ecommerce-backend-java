package com.tota.ecommercebackend.api.controller.user;

import com.tota.ecommercebackend.model.Address;
import com.tota.ecommercebackend.model.LocalUser;
import com.tota.ecommercebackend.model.dao.AddressDao;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    /** The Address DAO. */
    private AddressDao addressDAO;

    /**
     * Constructor for spring injection.
     * @param addressDAO
     */
    public UserController(AddressDao addressDAO) {
        this.addressDAO = addressDAO;
    }

    @GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress( @AuthenticationPrincipal LocalUser user, @PathVariable Long userId) {
        if(!userHasPermission(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressDAO.findByUser_Id(userId));
    }

    @PutMapping("/{userId}/address")
    public ResponseEntity<Address>putAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @RequestBody Address address){
        if(!userHasPermission(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser refUser = new LocalUser();
        refUser.setId(userId);
        address.setUser(refUser);
        return ResponseEntity.ok(addressDAO.save(address));
    }

    @PatchMapping("/{userId}/address/{addressId}")
    public ResponseEntity<Address>patchAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @PathVariable Long addressId, @RequestBody Address address){
        if(!userHasPermission(user, userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if(address.getId() == addressId){
            Optional<Address> opOriginalAddress = addressDAO.findById(addressId);
            if(opOriginalAddress.isPresent()){
                LocalUser originalUser = opOriginalAddress.get().getUser();
                if(originalUser.getId() == userId){
                    address.setUser(originalUser);
                    return ResponseEntity.ok(addressDAO.save(address));
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }


    private boolean userHasPermission(LocalUser user, Long id){
        return user.getId() == id;
    }

}
