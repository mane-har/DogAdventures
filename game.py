import pygame
import random

# Initialize Pygame
pygame.init()

# Constants
SCREEN_WIDTH = 800
SCREEN_HEIGHT = 600
PLAYER_SIZE = 40
PLATFORM_HEIGHT = 20
GRAVITY = 0.8
JUMP_FORCE = -15
PLATFORM_SPEED = 2

# Colors
WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
RED = (255, 0, 0)
GREEN = (0, 255, 0)
BLUE = (0, 0, 255)
BROWN = (139, 69, 19)

# Set up the display
screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
pygame.display.set_caption("Dog's Journey Home")

class Player:
    def __init__(self):
        self.x = 100
        self.y = SCREEN_HEIGHT - 100
        self.width = PLAYER_SIZE
        self.height = PLAYER_SIZE
        self.velocity_y = 0
        self.jumping = False
        self.on_ground = False

    def move(self, platforms):
        # Apply gravity
        self.velocity_y += GRAVITY
        self.y += self.velocity_y

        # Check for platform collisions
        self.on_ground = False
        for platform in platforms:
            if (self.y + self.height >= platform.y and 
                self.y + self.height <= platform.y + platform.height and
                self.x + self.width > platform.x and 
                self.x < platform.x + platform.width):
                self.y = platform.y - self.height
                self.velocity_y = 0
                self.on_ground = True
                break

        # Keep player in bounds
        if self.y > SCREEN_HEIGHT - self.height:
            self.y = SCREEN_HEIGHT - self.height
            self.velocity_y = 0
            self.on_ground = True

    def jump(self):
        if self.on_ground:
            self.velocity_y = JUMP_FORCE
            self.jumping = True

    def draw(self, screen):
        pygame.draw.rect(screen, BROWN, (self.x, self.y, self.width, self.height))

class Platform:
    def __init__(self, x, y, width):
        self.x = x
        self.y = y
        self.width = width
        self.height = PLATFORM_HEIGHT

    def move(self):
        self.x -= PLATFORM_SPEED
        if self.x + self.width < 0:
            self.x = SCREEN_WIDTH
            self.y = random.randint(200, SCREEN_HEIGHT - 100)

    def draw(self, screen):
        pygame.draw.rect(screen, GREEN, (self.x, self.y, self.width, self.height))

class Owner:
    def __init__(self):
        self.x = SCREEN_WIDTH - 100
        self.y = SCREEN_HEIGHT - 100
        self.width = PLAYER_SIZE
        self.height = PLAYER_SIZE

    def draw(self, screen):
        pygame.draw.rect(screen, BLUE, (self.x, self.y, self.width, self.height))

def main():
    clock = pygame.time.Clock()
    player = Player()
    owner = Owner()
    platforms = [
        Platform(0, SCREEN_HEIGHT - 40, SCREEN_WIDTH),
        Platform(300, 400, 200),
        Platform(100, 300, 200),
        Platform(500, 200, 200),
    ]
    running = True
    game_over = False

    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_SPACE:
                    player.jump()

        if not game_over:
            # Move platforms
            for platform in platforms:
                platform.move()

            # Move player
            keys = pygame.key.get_pressed()
            if keys[pygame.K_LEFT]:
                player.x -= 5
            if keys[pygame.K_RIGHT]:
                player.x += 5

            player.move(platforms)

            # Check for collision with owner
            if (player.x < owner.x + owner.width and
                player.x + player.width > owner.x and
                player.y < owner.y + owner.height and
                player.y + player.height > owner.y):
                game_over = True

        # Draw everything
        screen.fill(WHITE)
        for platform in platforms:
            platform.draw(screen)
        player.draw(screen)
        owner.draw(screen)

        if game_over:
            font = pygame.font.Font(None, 74)
            text = font.render('You Win!', True, BLACK)
            screen.blit(text, (SCREEN_WIDTH//2 - 100, SCREEN_HEIGHT//2))

        pygame.display.flip()
        clock.tick(60)

    pygame.quit()

if __name__ == "__main__":
    main() 