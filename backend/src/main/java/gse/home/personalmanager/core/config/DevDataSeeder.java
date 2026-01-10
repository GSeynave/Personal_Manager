package gse.home.personalmanager.core.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import gse.home.personalmanager.accounting.domain.model.Transaction;
import gse.home.personalmanager.accounting.domain.model.TransactionCategory;
import gse.home.personalmanager.accounting.domain.model.TransactionType;
import gse.home.personalmanager.accounting.domain.model.Wallet;
import gse.home.personalmanager.accounting.domain.model.WalletPermission;
import gse.home.personalmanager.accounting.domain.model.WalletRole;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionCategoryRepository;
import gse.home.personalmanager.accounting.infrastructure.repository.TransactionRepository;
import gse.home.personalmanager.accounting.infrastructure.repository.WalletPermissionRepository;
import gse.home.personalmanager.accounting.infrastructure.repository.WalletRepository;
import gse.home.personalmanager.user.domain.model.AppUser;
import gse.home.personalmanager.user.domain.model.Tenant;
import gse.home.personalmanager.user.infrastructure.repository.TenantRepository;
import gse.home.personalmanager.user.infrastructure.repository.UserRepository;

@Configuration
@Profile("dev")
public class DevDataSeeder {

  @Bean
  CommandLineRunner seedDevData(
      TenantRepository tenantRepo,
      UserRepository userRepo,
      WalletRepository walletRepo,
      WalletPermissionRepository permissionRepo,
      TransactionCategoryRepository categoryRepo,
      TransactionRepository transactionRepo) {

    return args -> {
      // Check if data already exists
      if (userRepo.count() > 0) {
        return;
      }

      // Create tenants
      Tenant tenant1 = new Tenant();
      tenant1 = tenantRepo.save(tenant1);

      Tenant tenant2 = new Tenant();
      tenant2 = tenantRepo.save(tenant2);

      // Create users
      AppUser user1 = new AppUser();
      user1.setFirebaseUid("user1");
      user1.setEmail("user1@test.com");
      user1.setUserTag("testuser1");
      user1.setRole("ROLE_USER");
      user1.setTenant(tenant1);
      user1 = userRepo.save(user1);

      AppUser user2 = new AppUser();
      user2.setFirebaseUid("user2");
      user2.setEmail("user2@test.com");
      user2.setUserTag("testuser2");
      user2.setRole("ROLE_USER");
      user2.setTenant(tenant1);
      user2 = userRepo.save(user2);

      AppUser user3 = new AppUser();
      user3.setFirebaseUid("user3");
      user3.setEmail("user3@test.com");
      user3.setUserTag("testuser3");
      user3.setRole("ROLE_USER");
      user3.setTenant(tenant2);
      user3 = userRepo.save(user3);

      // Create wallets
      Wallet wallet1 = new Wallet();
      wallet1.setName("User1 Personal Wallet");
      wallet1.setDescription("Main wallet for user1");
      wallet1.setOwner(user1);
      wallet1.setTenant(tenant1);
      wallet1 = walletRepo.save(wallet1);

      Wallet wallet2 = new Wallet();
      wallet2.setName("User1 Secret Wallet");
      wallet2.setDescription("Private wallet");
      wallet2.setOwner(user1);
      wallet2.setTenant(tenant1);
      wallet2 = walletRepo.save(wallet2);

      Wallet wallet3 = new Wallet();
      wallet3.setName("User2 Personal Wallet");
      wallet3.setDescription("Main wallet for user2");
      wallet3.setOwner(user2);
      wallet3.setTenant(tenant1);
      wallet3 = walletRepo.save(wallet3);

      Wallet wallet4 = new Wallet();
      wallet4.setName("User3 Personal Wallet");
      wallet4.setDescription("Main wallet for user3");
      wallet4.setOwner(user3);
      wallet4.setTenant(tenant2);
      wallet4 = walletRepo.save(wallet4);

      // Create permissions
      WalletPermission perm1 = new WalletPermission();
      perm1.setUser(user2);
      perm1.setWallet(wallet1);
      perm1.setRolePermission(WalletRole.READ);
      permissionRepo.save(perm1);

      WalletPermission perm2 = new WalletPermission();
      perm2.setUser(user2);
      perm2.setWallet(wallet2);
      perm2.setRolePermission(WalletRole.NONE);
      permissionRepo.save(perm2);

      WalletPermission perm3 = new WalletPermission();
      perm3.setUser(user3);
      perm3.setWallet(wallet1);
      perm3.setRolePermission(WalletRole.WRITE);
      permissionRepo.save(perm3);

      TransactionCategory category1 = new TransactionCategory();
      category1.setTitle("Initial Deposit");
      category1.setDescription("Initial deposit category");
      category1.setIcon("💰");
      category1.setExpectedAmount(1000.0);
      category1 = categoryRepo.save(category1);

      Transaction tx1 = new Transaction();
      tx1.setAmount(100.0);
      tx1.setDate(java.time.LocalDate.now().minusDays(10));
      tx1.setImportLabel("Import Initial Deposit");
      tx1.setCustomLabel("Custom Initial Deposit");
      tx1.setAmount(1000.0);
      tx1.setType(TransactionType.CREDIT);
      tx1.setCategory(category1);
      tx1.setWallet(wallet1);
      tx1.setCurrentBalance(223.21);
      tx1.setUser(user1);
      transactionRepo.save(tx1);

      System.out.println("✅ Dev test data seeded successfully!");
    };
  }
}
