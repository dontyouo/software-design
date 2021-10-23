package battleship;

import java.util.List;
import java.util.Random;

public class OceanMap {

  public static final char SYMBOL_OF_OCEAN = 'o';
  public static final char SYMBOL_OF_SHIP = 's';
  public static final char SYMBOL_OF_PLACE_AROUND_SHIP = 'x';

  private final int rows;
  private final int columns;
  private final List<Ship> ships;
  private final List<Rocket> rockets;

  private final char[][] map;

  OceanMap(int rows, int columns, List<Ship> ships, List<Rocket> rockets) {
    this.rows = rows;
    this.columns = columns;
    this.ships = ships;
    this.rockets = rockets;
    map = new char[rows][columns];

    clearMap();
  }


  public void printMap() {
    rewriteMap();
    int maxFormatLength = Math.max(Integer.toString(rows).length(),
        Integer.toString(columns).length()) + 1;

    for (int i = -1; i < rows; ++i) {
      for (int j = -1; j < columns; ++j) {

        if (i == -1) {
          System.out.format("%" + maxFormatLength + "d", Math.abs(j));
        } else if (j == -1) {
          System.out.format("%" + maxFormatLength + "d", Math.abs(i));
        } else {
          System.out.format("%" + maxFormatLength + "c", map[i][j]);
        }

      }
      System.out.println();
    }
  }


  private void rewriteMap() {
    clearMap();
    for (var rocket : rockets) {
      map[rocket.getY()][rocket.getX()] = SYMBOL_OF_PLACE_AROUND_SHIP;
    }

    for (var ship : ships) {
      for (var chunk : ship.getChunks()) {
        if (chunk.isHit()) {
          map[chunk.getY()][chunk.getX()] = SYMBOL_OF_SHIP;
        }
      }
      if (ship.isSunk()) {
        markPlaceAroundShip(ship);
      }
    }
  }


  private void clearMap() {
    for (int i = 0; i < rows; ++i) {
      for (int j = 0; j < columns; ++j) {
        map[i][j] = SYMBOL_OF_OCEAN;
      }
    }
  }


  public void positionShip(Ship ship) {
    Random rand = new Random();
    int randomAlignment;
    int randomX;
    int randomY;
    boolean isPositionFind = false;

    ChunkOfShip[] chunks = new ChunkOfShip[ship.getLength()];
    for (int i = 0; i < ship.getLength(); ++i) {
      chunks[i] = new ChunkOfShip(0, 0);
    }

    for (int i = 0; i < 4000; ++i) {
      randomAlignment = rand.nextInt(0, 4);
      randomX = rand.nextInt(0, columns);
      randomY = rand.nextInt(0, rows);

      switch (randomAlignment) {
        case 0 -> {
          if (randomY - ship.getLength() + 1 >= 0) {
            isPositionFind = true;
            for (int j = 0; j < ship.getLength(); ++j) {
              if (map[randomY - j][randomX] != SYMBOL_OF_OCEAN) {
                isPositionFind = false;
                break;
              }
              chunks[ship.getLength() - j - 1].setX(randomX);
              chunks[ship.getLength() - j - 1].setY(randomY - j);
            }
          }
        }
        case 1 -> {
          if (randomX - ship.getLength() + 1 >= 0) {
            isPositionFind = true;
            for (int j = 0; j < ship.getLength(); ++j) {
              if (map[randomY][randomX - j] != SYMBOL_OF_OCEAN) {
                isPositionFind = false;
                break;
              }
              chunks[ship.getLength() - j - 1].setX(randomX - j);
              chunks[ship.getLength() - j - 1].setY(randomY);
            }
          }
        }
        case 2 -> {
          if (randomY + ship.getLength() - 1 < rows) {
            isPositionFind = true;
            for (int j = 0; j < ship.getLength(); ++j) {
              if (map[randomY + j][randomX] != SYMBOL_OF_OCEAN) {
                isPositionFind = false;
                break;
              }
              chunks[j].setX(randomX);
              chunks[j].setY(randomY + j);
            }
          }
        }
        case 3 -> {
          if (randomX + ship.getLength() - 1 < columns) {
            isPositionFind = true;
            for (int j = 0; j < ship.getLength(); ++j) {
              if (map[randomY][randomX + j] != SYMBOL_OF_OCEAN) {
                isPositionFind = false;
                break;
              }
              chunks[j].setX(randomX + j);
              chunks[j].setY(randomY);
            }
          }
        }
      }

      if (isPositionFind) {
        ship.setChunks(chunks);

        for (var chunk : chunks) {
          map[chunk.getY()][chunk.getX()] = SYMBOL_OF_SHIP;
        }

        ship.setAlignment((randomAlignment % 2 == 0) ? Alignment.VERTICAL : Alignment.HORIZONTAL);

        markPlaceAroundShip(ship);
        break;
      }
    }

    if (!isPositionFind) {
      throw new StackOverflowError("A lot of ships");
    }

  }

  private void markPlaceAroundShip(Ship ship) {
    switch (ship.getAlignment()) {
      case HORIZONTAL -> markPlaceAroundHorizontalShip(ship);
      case VERTICAL -> markPlaceAroundVerticalShip(ship);
    }
  }


  private void markPlaceAroundHorizontalShip(Ship ship) {
    for (int i = 0; i < ship.getLength(); ++i) {
      var currentChunk = ship.getChunks()[i];

      if ((i == 0) && (currentChunk.getX() - 1 >= 0)) {
        map[currentChunk.getY()][currentChunk.getX()
            - 1] =
            SYMBOL_OF_PLACE_AROUND_SHIP;
        if (currentChunk.getY() - 1 >= 0) {
          map[currentChunk.getY() - 1][currentChunk.getX()
              - 1] =
              SYMBOL_OF_PLACE_AROUND_SHIP;
        }
        if (currentChunk.getY() + 1 < rows) {
          map[currentChunk.getY() + 1][currentChunk.getX()
              - 1] =
              SYMBOL_OF_PLACE_AROUND_SHIP;
        }
      }

      if ((i == ship.getLength() - 1) && (currentChunk.getX() + 1 < columns)) {
        map[currentChunk.getY()][currentChunk.getX()
            + 1] =
            SYMBOL_OF_PLACE_AROUND_SHIP;
        if (currentChunk.getY() - 1 >= 0) {
          map[currentChunk.getY() - 1][currentChunk.getX()
              + 1] =
              SYMBOL_OF_PLACE_AROUND_SHIP;
        }
        if (currentChunk.getY() + 1 < rows) {
          map[currentChunk.getY() + 1][currentChunk.getX()
              + 1] =
              SYMBOL_OF_PLACE_AROUND_SHIP;
        }
      }

      if (currentChunk.getY() - 1 >= 0) {
        map[currentChunk.getY()
            - 1][currentChunk.getX()] =
            SYMBOL_OF_PLACE_AROUND_SHIP;
      }
      if (currentChunk.getY() + 1 < rows) {
        map[currentChunk.getY()
            + 1][currentChunk.getX()] =
            SYMBOL_OF_PLACE_AROUND_SHIP;
      }
    }
  }


  private void markPlaceAroundVerticalShip(Ship ship) {
    for (int i = 0; i < ship.getLength(); ++i) {
      var currentChunk = ship.getChunks()[i];

      if ((i == 0) && (currentChunk.getY() - 1 >= 0)) {
        map[currentChunk.getY()
            - 1][currentChunk.getX()] =
            SYMBOL_OF_PLACE_AROUND_SHIP;
        if (currentChunk.getX() - 1 >= 0) {
          map[currentChunk.getY() - 1][currentChunk.getX()
              - 1] =
              SYMBOL_OF_PLACE_AROUND_SHIP;
        }
        if (currentChunk.getX() + 1 < columns) {
          map[currentChunk.getY() - 1][currentChunk.getX()
              + 1] =
              SYMBOL_OF_PLACE_AROUND_SHIP;
        }
      }

      if ((i == ship.getLength() - 1) && (currentChunk.getY() + 1 < rows)) {
        map[currentChunk.getY()
            + 1][currentChunk.getX()] =
            SYMBOL_OF_PLACE_AROUND_SHIP;
        if (currentChunk.getX() - 1 >= 0) {
          map[currentChunk.getY() + 1][currentChunk.getX()
              - 1] =
              SYMBOL_OF_PLACE_AROUND_SHIP;
        }
        if (currentChunk.getX() + 1 < columns) {
          map[currentChunk.getY() + 1][currentChunk.getX()
              + 1] =
              SYMBOL_OF_PLACE_AROUND_SHIP;
        }
      }

      if (currentChunk.getX() - 1 >= 0) {
        map[currentChunk.getY()][currentChunk.getX()
            - 1] =
            SYMBOL_OF_PLACE_AROUND_SHIP;
      }
      if (currentChunk.getX() + 1 < columns) {
        map[currentChunk.getY()][currentChunk.getX()
            + 1] =
            SYMBOL_OF_PLACE_AROUND_SHIP;
      }
    }
  }

}