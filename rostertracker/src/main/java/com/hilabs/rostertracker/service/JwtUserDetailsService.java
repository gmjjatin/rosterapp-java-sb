package com.hilabs.rostertracker.service;

import com.hilabs.rostertracker.config.JwtTokenUtil;
import com.hilabs.roster.entity.RosterUser;
import com.hilabs.roster.entity.RosterUserXRole;
import com.hilabs.rostertracker.model.LoginDetails;
import com.hilabs.rostertracker.model.UserDTO;
import com.hilabs.rostertracker.repository.RosterRoleRepository;
import com.hilabs.rostertracker.repository.RosterUserXRoleRepository;
import com.hilabs.rostertracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RosterRoleRepository rosterRoleRepository;

    @Autowired
    private RosterUserXRoleRepository rosterUserXRoleRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${defaultPassword}")
    private String defaultPassword;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        RosterUser user = userRepository.findActiveUserByUserId(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + userId);
        }
        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(),
                new ArrayList<>());
    }

    //UserDao --> UserRepository
    //UserDTO --> USerDTO
    //DAOUser --> RosterUser
    @Transactional
    public RosterUser save(UserDTO user) {
        RosterUser newUser = new RosterUser();
        newUser.setUserId(user.getUserId().toLowerCase());
        newUser.setPassword(bcryptEncoder.encode(defaultPassword));
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setCreatedDate(new Date());
        //doubt here
        newUser.setCreatedUserId(user.getCreatedORUpdatedUserId());
        newUser.setActiveFlag(1);

        RosterUserXRole rosterUserXRole = new RosterUserXRole();
        rosterUserXRole.setUserId(user.getUserId().toLowerCase());
        rosterUserXRole.setRoleCD(user.getRoleCd());
        rosterUserXRole.setCreatedDate(new Date());
        //doubt here
        rosterUserXRole.setCreatedUserId(user.getCreatedORUpdatedUserId());

        // if(userRepository.findByUserId(user.getUserId().toLowerCase()) != null) {
        //  userRepository.deleteInActiveUser(user.getUserId().toLowerCase());
        // }

        rosterUserXRoleRepository.save(rosterUserXRole);
        return userRepository.save(newUser);
    }

    @Transactional
    public void updatePassword(String userId,String newPassword) {
        RosterUser rosterUser =userRepository.findActiveUserByUserId(userId);
        rosterUser.setPassword(bcryptEncoder.encode(newPassword));
        userRepository.save(rosterUser);
        //userRepository.updatePassword(userId,bcryptEncoder.encode(newPassword));
        return;
    }

    public LoginDetails getLoginDetails(UserDetails userDetails, String username, String pwd) {
        final String token = jwtTokenUtil.generateToken(userDetails);
        String role = rosterUserXRoleRepository.getRole(username);
        RosterUser rosterUser =userRepository.findActiveUserByUserId(username);
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setToken(token);
        loginDetails.setUserId(username);
        loginDetails.setRoleCD(role);
        loginDetails.setFirstName(rosterUser.getFirstName());
        loginDetails.setIsDefaultPassword(false);
        if(defaultPassword.equals(pwd)) {
            loginDetails.setIsDefaultPassword(true);
        }
        return loginDetails;
    }

    public RosterUser findUserbyUserId(String userId) {
        RosterUser rosterUser = userRepository.findByUserId(userId);
        return rosterUser;
    }

    @Transactional
    public RosterUser updateExisting(RosterUser isExistingUser, UserDTO user) {
        rosterUserXRoleRepository.updateRoleCd(isExistingUser.getUserId(), user.getRoleCd(), new Date(),user.getCreatedORUpdatedUserId() );
        return userRepository.save(isExistingUser);
    }

    @Transactional
    public void resetPassword(String userId) {
        //userRepository.updatePassword(userId,bcryptEncoder.encode(defaultPassword));
        RosterUser rosterUser =userRepository.findActiveUserByUserId(userId);
        rosterUser.setPassword(bcryptEncoder.encode(defaultPassword));
        userRepository.save(rosterUser);
        return;
    }

    @Transactional
    public void deleteUser(String userId) {
        RosterUser rosterUser = userRepository.findActiveUserByUserId(userId);
        rosterUser.setActiveFlag(0);
        userRepository.save(rosterUser);
    }


    public String generateRefreshToken(Map<String, Object> claims, String subject) {
        return jwtTokenUtil.doGenerateRefreshToken(claims, subject);
    }

    public List<String> getRoles() {
        // TODO Auto-generated method stub
        return rosterRoleRepository.getRoles();
    }

    //TODO manikanta
    public List<UserDTO> getUsers() {
        // TODO Auto-generated method stub to test
        //remove comment for commit test
        List<UserDTO> userDTOList = new ArrayList<>();
        List<Object[]> rosterUsers =  userRepository.getUsersWithRoles();
        if(null != rosterUsers && !rosterUsers.isEmpty()) {
            for(Object[] obj : rosterUsers) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(stringNullCheck(obj[0]));
                userDTO.setFirstName(stringNullCheck(obj[1]));
                userDTO.setLastName(stringNullCheck(obj[2]));
                userDTO.setRoleCd(stringNullCheck(obj[3]));
                userDTOList.add(userDTO);
            }
        }
        return userDTOList;
    }

    public static String stringNullCheck(Object result) {
        return result != null ? result.toString() : null;
    }

}