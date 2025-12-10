<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { moduleColor, hexToRgbStr, moduleTintAlpha } from '@/config/moduleColors'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { User, Mail, Tag, Key, LogOut } from 'lucide-vue-next'

const authStore = useAuthStore()
const router = useRouter()

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<template>
  <main
    :style="{
      '--module-color': moduleColor('todo'),
      '--module-color-rgb': hexToRgbStr(moduleColor('todo')),
      '--module-tint-alpha': moduleTintAlpha(moduleColor('todo')),
    }"
    class="p-6 min-h-screen space-y-8"
  >
    <!-- Page Header -->
    <div class="page-header">
      <div class="flex items-center gap-3 mb-2">
        <User class="w-8 h-8 text-productivity" />
        <h1 class="text-3xl font-bold text-foreground">My Profile</h1>
      </div>
      <p class="text-sm text-productivity">Manage your account information</p>
    </div>

    <!-- Content Area -->
    <div class="max-w-2xl mx-auto space-y-6">
      <!-- Account Information Card -->
      <Card>
        <CardHeader>
          <CardTitle>Account Information</CardTitle>
          <CardDescription>Your personal account details</CardDescription>
        </CardHeader>
        <CardContent class="space-y-4">
          <!-- Email -->
          <div class="flex items-center justify-between p-4 bg-card-item rounded-lg border border-border">
            <div class="flex items-center gap-3">
              <Mail class="w-5 h-5 text-muted-foreground" />
              <div>
                <p class="text-sm font-medium text-foreground">Email</p>
                <p class="text-sm text-muted-foreground">{{ authStore.userEmail || 'Not available' }}</p>
              </div>
            </div>
          </div>

          <!-- User Tag -->
          <div class="flex items-center justify-between p-4 bg-card-item rounded-lg border border-border">
            <div class="flex items-center gap-3">
              <Tag class="w-5 h-5 text-muted-foreground" />
              <div>
                <p class="text-sm font-medium text-foreground">User Tag</p>
                <Badge variant="secondary" class="mt-1">
                  {{ authStore.userIdentity?.userTag || 'Not set' }}
                </Badge>
              </div>
            </div>
          </div>

          <!-- User ID -->
          <div class="flex items-center justify-between p-4 bg-card-item rounded-lg border border-border">
            <div class="flex items-center gap-3">
              <Key class="w-5 h-5 text-muted-foreground" />
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-foreground">User ID</p>
                <p class="text-xs font-mono text-muted-foreground truncate">
                  {{ authStore.userId || 'Not available' }}
                </p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Actions Card -->
      <Card>
        <CardHeader>
          <CardTitle>Account Actions</CardTitle>
          <CardDescription>Manage your session and account settings</CardDescription>
        </CardHeader>
        <CardContent>
          <Button 
            variant="destructive" 
            class="w-full"
            @click="handleLogout"
          >
            <LogOut class="w-4 h-4 mr-2" />
            Logout
          </Button>
        </CardContent>
      </Card>
    </div>
  </main>
</template>

<style scoped>
main {
  padding: var(--spacing-lg, 24px);
  position: relative;
  min-height: 100vh;
}

main::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  border-radius: 4px 0 0 4px;
}

.page-header {
  margin-bottom: 24px;
  padding-left: 12px;
}
</style>
