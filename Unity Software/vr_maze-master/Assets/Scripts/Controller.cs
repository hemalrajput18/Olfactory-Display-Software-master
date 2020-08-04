using Assets.Scripts;
using Assets.Scripts.DAO;
using System.IO;
using UnityEngine;

[RequireComponent(typeof(MazeConstructor))]
[RequireComponent(typeof(TCP_IO))]

public class Controller : MonoBehaviour
{

    #region public members
    public FpsMovement player;
    public TextAsset config;
    public MazeDataParser parser;
    public static CommunicationLayer communicationLayer;
    public static bool isDebug = true;
    public static StreamWriter writer = new StreamWriter("log.txt");
    #endregion

    #region private members
    private MazeConstructor generator;
    #endregion

    #region methods
    void Start()
    {
        generator = GetComponent<MazeConstructor>();
        StartNewGame();
    }

    private void StartNewGame()
    {
        StartNewMaze();
    }

    private void StartNewMaze()
    {
        generator.GenerateNewMaze(config);

        if (parser.commType == "TCP/IP")
        {
            communicationLayer = new TCP_IO();
        }
        else if(parser.commType == "MIDI")
        {
            communicationLayer = new MIDI_IO();
        }

        communicationLayer.Init();

        float x = (generator.StartCell.x - 1) * parser.cellWidth + parser.cellWidth / 2.0f;
        float y = 1;
        float z = (generator.StartCell.y - 1) * parser.cellWidth + parser.cellWidth / 2.0f;
        player.transform.position = new Vector3(x, y, z);

        player.enabled = true;
    }
    #endregion

}
