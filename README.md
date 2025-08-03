[README.md](https://github.com/user-attachments/files/21567538/README.md)

# SimpleBoards

SimpleBoards is a developer dependency for PaperMC developers to create custom
player-specific sidebars and tab prefixes

## Installation

## Usage via JitPack

### Step 1: Add the JitPack repository

Maven
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Gradle (Groovy)
```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

Gradle (Kotlin DSL)
```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

### Step 2: Add the dependency
Maven
```xml
	<dependency>
	    <groupId>com.github.KleinBuli</groupId>
	    <artifactId>SimpleBoard</artifactId>
	    <version>1.0.0</version>
	</dependency>
```

Gradle (Groovy)
```gradle
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

```gradle (Kotlin DSL)
	dependencies {
	        implementation 'com.github.KleinBuli:SimpleBoard:1.0.0'
	}
```
## Examples (Sidebar)

Creating a new SideBar
```java
Sidebar bar = new SideBar("GameSideBar").title(Component.text("SuperFunGame"));
bar.builder(() -> List.of(Component.text("Players: " + somePlayerList.size());
```

You can also use kyori's MiniMessage API to use custom RGB colors.

```java
bar.builder(() -> List.of(Sidebar.mm("<red>This is super cool red text!</red>")));
```

Showing / Hiding the scoreboard works like this:

```java
bar.show(player);
bar.hide(player);
```

There are two different ways to update the scoreboard

either you use
```java
bar.update()
```
to update the scoreboard manually - or you use

```java
bar.startUpdating(javaPlugin, periodTicks)
```

to start a scheduler which updates the scoreboard every few ticks.

## Examples (Tablist)

To create a new tablist Prefix do
```java
Prefix adminPrefix = new Prefix(Sidebar.mm("<red>[Admin]</red> <dark_gray>|</dark_gray>", 0));

```

The Integer describes the priority in the tablist and helps to sort the tablist. 
The priorities are sorted "alphabetically" since they'll be parsed into a String later by the api,
which means Priority 10 is a lower priority than Priority 9 (compare first digits). 
Priority 91 would be the next higher one.
After creating a new Prefix, register it (e.g in the onEnable()-Method) by using the Prefix Registry.

```java 
PrefixRegistry.registerPrefix("admin", adminPrefix);
```

You can get this prefix by using
```java
Optional<Prefix> adminOptional = PrefixRegistry.getPrefix("admin");
if(adminOptional.isPresent()) {
  Prefix adminPrefix = adminOptional.get();
}
```

There are 2 ways to apply a prefix to a player:

```java
  Prefix adminPrefix = adminOptional.get();
  adminPrefix.applyTo(player);
```
```java
  Prefix.setPrefix(player, adminPrefix);

```

To update the Tablist Prefixes, use the PrefixHandler class.
```java
  PrefixHandler.updateTablist();
```
