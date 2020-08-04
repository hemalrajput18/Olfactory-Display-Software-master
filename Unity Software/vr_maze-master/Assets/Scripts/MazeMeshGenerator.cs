﻿using Assets.Scripts;
using System.Collections.Generic;
using UnityEngine;

public class MazeMeshGenerator : MonoBehaviour
{

    #region public members
    public Material floorMaterial;
    public Material ceilingMaterial;
    public Material northMaterial;
    public Material southMaterial;
    public Material eastMaterial;
    public Material westMaterial;
    public Material cornerMaterial;
    public MazeDataParser parser;
    #endregion

    #region methods
    /// <summary>
    /// reads through cell data in order to generate the mesh
    /// </summary>
    /// <param name="data">cell data parsed from config file</param>
    /// <returns>final mesh</returns>
    public GameObject FromData(MazeDataParser.MazeCell[,] data)
    {
        var go = new GameObject();
        go.transform.position = Vector3.zero;
        go.name = "Procedural Maze";
        go.tag = "Generated";
        var maze = new Mesh();

        // Generate maze mesh and setup GameObject and necessary components.
        var newVertices = new List<Vector3>();
        var newUVs = new List<Vector2>();

        maze.subMeshCount = 7;
        var floorTriangles = new List<int>();
        var ceilingTriangles = new List<int>();
        var northWallTriangles = new List<int>();
        var southWallTriangles = new List<int>();
        var eastWallTriangles = new List<int>();
        var westWallTriangles = new List<int>();
        var cornerTriangles = new List<int>();

        var rMax = data.GetUpperBound(0);
        var cMax = data.GetUpperBound(1);
        var width = parser.cellWidth;
        var height = parser.cellHeight;
        var halfWallWidth = (width * parser.wallFraction) / 2.0f;

        var mapOriginX = -2f * width;
        var mapOriginY = -2f * width;

        for (var i = 1; i < rMax; i++)
        {
            var cellOriginY = i * width + mapOriginY;
            var southY = cellOriginY + halfWallWidth;
            var northY = cellOriginY + (width - halfWallWidth);

            for (int j = 1; j < cMax; j++)
            {
                var cellOriginX = j * width + mapOriginX;
                var westX = cellOriginX + halfWallWidth;
                var eastX = cellOriginX + (width - halfWallWidth);

                #region Floor Generation
                //generate floor
                Utility.GraphicsUtils.AddQuad(new List<Vector3>()
                {
                    new Vector3(cellOriginX, 0f, cellOriginY),
                    new Vector3(cellOriginX + width, 0f, cellOriginY),
                    new Vector3(cellOriginX + width, 0f, cellOriginY + width),
                    new Vector3(cellOriginX, 0f, cellOriginY + width),
                }, ref newVertices, ref newUVs, ref floorTriangles);
                #endregion

                #region Ceiling Generation
                if (i > 1 && j > 1 && i < (rMax - 1) && j < (cMax - 1))
                {
                    // Add the ceiling
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>()
                    {
                        new Vector3(cellOriginX, height, cellOriginY),
                        new Vector3(cellOriginX, height, cellOriginY + width),
                        new Vector3(cellOriginX + width, height, cellOriginY + width),
                        new Vector3(cellOriginX + width, height, cellOriginY),
                    },
                    ref newVertices, ref newUVs, ref ceilingTriangles);
                }
                #endregion

                // Get the neighbourhood
                MazeDataParser.MazeCell cell = data[i, j];
                MazeDataParser.MazeCell southBound = data[i - 1, j];
                MazeDataParser.MazeCell westBound = data[i, j - 1];
                MazeDataParser.MazeCell eastBound = data[i, j + 1];
                MazeDataParser.MazeCell northBound = data[i + 1, j];

                // Build the main wall sections if needed.
                if (cell.north != 0)
                {
                    // Build north wall
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(cellOriginX, 0f, northY),
                        new Vector3(cellOriginX + width, 0f, northY),
                        new Vector3(cellOriginX + width, height, northY),
                        new Vector3(cellOriginX, height, northY)
                    },
                    ref newVertices, ref newUVs, ref northWallTriangles);
                }

                if (cell.south != 0)
                {
                    // Build south wall
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(cellOriginX, 0f, southY),
                        new Vector3(cellOriginX, height, southY),
                        new Vector3(cellOriginX + width, height, southY),
                        new Vector3(cellOriginX + width, 0f, southY)
                    },
                    ref newVertices, ref newUVs, ref southWallTriangles);
                }

                if (cell.east != 0)
                {
                    // Build east wall
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(eastX, 0f, cellOriginY),
                        new Vector3(eastX, height, cellOriginY),
                        new Vector3(eastX, height, cellOriginY + width),
                        new Vector3(eastX, 0f, cellOriginY + width)
                    },
                    ref newVertices, ref newUVs, ref eastWallTriangles);
                }

                if (cell.west != 0)
                {
                    // Build west wall
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(westX, 0f, cellOriginY),
                        new Vector3(westX, 0f, cellOriginY + width),
                        new Vector3(westX, height, cellOriginY + width),
                        new Vector3(westX, height, cellOriginY)
                    },
                    ref newVertices, ref newUVs, ref eastWallTriangles);
                }

                // Build the corner in-fills where necessary.
                // north-west
                if (cell.north == 0 && cell.west == 0 && (northBound.west != 0 || westBound.north != 0))
                {
                    // Build north wall in-fill
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(cellOriginX, 0f, northY),
                        new Vector3(cellOriginX + halfWallWidth, 0f, northY),
                        new Vector3(cellOriginX + halfWallWidth, height, northY),
                        new Vector3(cellOriginX, height, northY)
                    },
                    ref newVertices, ref newUVs, ref northWallTriangles);

                    // Build west wall in-fill
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(westX, 0f, cellOriginY + (width - halfWallWidth)),
                        new Vector3(westX, 0f, cellOriginY + width),
                        new Vector3(westX, height, cellOriginY + width),
                        new Vector3(westX, height, cellOriginY + (width - halfWallWidth))
                    },
                    ref newVertices, ref newUVs, ref eastWallTriangles);
                }

                // north-east
                if (cell.north == 0 && cell.east == 0 && (northBound.east != 0 || eastBound.north != 0))
                {
                    // Build north wall in-fill
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(cellOriginX + (width - halfWallWidth), 0f, northY),
                        new Vector3(cellOriginX + width, 0f, northY),
                        new Vector3(cellOriginX + width, height, northY),
                        new Vector3(cellOriginX + (width - halfWallWidth), height, northY)
                    },
                    ref newVertices, ref newUVs, ref northWallTriangles);

                    // Build east wall in-fill
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(eastX, 0f, cellOriginY + (width - halfWallWidth)),
                        new Vector3(eastX, height, cellOriginY + (width - halfWallWidth)),
                        new Vector3(eastX, height, cellOriginY + width),
                        new Vector3(eastX, 0f, cellOriginY + width)
                    },
                    ref newVertices, ref newUVs, ref eastWallTriangles);
                }

                // south-east
                if (cell.south == 0 && cell.east == 0 && (eastBound.south != 0 || southBound.east != 0))
                {
                    // Build south wall in-fill
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(cellOriginX + (width - halfWallWidth), 0f, southY),
                        new Vector3(cellOriginX + (width - halfWallWidth), height, southY),
                        new Vector3(cellOriginX + width, height, southY),
                        new Vector3(cellOriginX + width, 0f, southY)
                    },
                    ref newVertices, ref newUVs, ref southWallTriangles);

                    // Build east wall in-fill
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(eastX, 0f, cellOriginY),
                        new Vector3(eastX, height, cellOriginY),
                        new Vector3(eastX, height, cellOriginY + halfWallWidth),
                        new Vector3(eastX, 0f, cellOriginY + halfWallWidth)
                    },
                    ref newVertices, ref newUVs, ref eastWallTriangles);
                }

                // south-west
                if (cell.south == 0 && cell.west == 0 && (westBound.south != 0 || southBound.west != 0))
                {
                    // Build south wall in-fill
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(cellOriginX, 0f, southY),
                        new Vector3(cellOriginX, height, southY),
                        new Vector3(cellOriginX + halfWallWidth, height, southY),
                        new Vector3(cellOriginX + halfWallWidth, 0f, southY)
                    },
                    ref newVertices, ref newUVs, ref southWallTriangles);

                    // Build west wall in-fill
                    Utility.GraphicsUtils.AddQuad(new List<Vector3>() {
                        new Vector3(westX, 0f, cellOriginY),
                        new Vector3(westX, 0f, cellOriginY + halfWallWidth),
                        new Vector3(westX, height, cellOriginY + halfWallWidth),
                        new Vector3(westX, height, cellOriginY)
                    },
                    ref newVertices, ref newUVs, ref eastWallTriangles);
                }
            }
        }

        maze.vertices = newVertices.ToArray();
        maze.uv = newUVs.ToArray();

        maze.SetTriangles(floorTriangles.ToArray(), 0);
        maze.SetTriangles(ceilingTriangles.ToArray(), 1);
        maze.SetTriangles(northWallTriangles.ToArray(), 2);
        maze.SetTriangles(southWallTriangles.ToArray(), 3);
        maze.SetTriangles(eastWallTriangles.ToArray(), 4);
        maze.SetTriangles(westWallTriangles.ToArray(), 5);
        maze.SetTriangles(cornerTriangles.ToArray(), 6);

        maze.RecalculateNormals();

        MeshFilter mf = go.AddComponent<MeshFilter>();
        mf.mesh = maze;

        MeshCollider mc = go.AddComponent<MeshCollider>();
        mc.sharedMesh = mf.mesh;

        MeshRenderer mr = go.AddComponent<MeshRenderer>();
        floorMaterial = Resources.Load<Material>("Materials/Floor") as Material;
        ceilingMaterial = Resources.Load<Material>("Materials/Ceiling") as Material;
        northMaterial = Resources.Load<Material>("Materials/NorthWall") as Material;
        southMaterial = Resources.Load<Material>("Materials/SouthWall") as Material;
        eastMaterial = Resources.Load<Material>("Materials/EastWall") as Material;
        westMaterial = Resources.Load<Material>("Materials/WestWall") as Material;
        cornerMaterial = Resources.Load<Material>("Materials/CornerMaterial") as Material;
        mr.materials = new Material[7] { floorMaterial, ceilingMaterial, northMaterial, southMaterial, eastMaterial, westMaterial, cornerMaterial };
        
        return go;
    }
    #endregion

}
