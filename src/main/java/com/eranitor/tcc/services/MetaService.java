package com.eranitor.tcc.services;

import com.eranitor.tcc.dto.DistribuicaoMateriaDTO;
import com.eranitor.tcc.entity.Desempenho;
import com.eranitor.tcc.entity.Materia;
import com.eranitor.tcc.entity.Usuario;
import com.eranitor.tcc.repository.DesempenhoRepository;
import com.eranitor.tcc.repository.MateriaRepository;
import com.eranitor.tcc.repository.UsuarioRepository;
import com.eranitor.tcc.services.ProgressoService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class MetaService {

    private static final int META_MINIMA = 1;
    private static final int META_MAXIMA = 168;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private DesempenhoRepository desempenhoRepository;

    @Autowired
    private ProgressoService progressoService;

    @Transactional
    public void setMetaSemanal(Long usuarioId, Integer horasTotal) {
        validarUsuarioId(usuarioId);
        validarMeta(horasTotal);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        usuario.setMetaSemanalHoras(horasTotal);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public List<DistribuicaoMateriaDTO> sugerirDistribuicao(Long usuarioId, Integer horasTotal) {
        validarUsuarioId(usuarioId);
        validarMeta(horasTotal);

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        }

        List<Materia> materias = materiaRepository.findAllByUsuario_IdUsuarioAndAtivaTrue(usuarioId);

        if (materias.isEmpty()) {
            throw new IllegalStateException("O usuário não possui matérias ativas para distribuir as horas.");
        }

        materias.forEach(this::validarDificuldade);

        int somaDificuldades = materias.stream()
                .mapToInt(Materia::getDificuldade)
                .sum();

        List<Parcela> parcelas = new ArrayList<>();
        int horasDistribuidas = 0;

        for (Materia materia : materias) {
            int peso = materia.getDificuldade();

            long numerador = (long) horasTotal * peso;
            int horasBase = (int) (numerador / somaDificuldades);
            long resto = numerador % somaDificuldades;

            parcelas.add(new Parcela(materia, horasBase, resto));
            horasDistribuidas += horasBase;
        }

        int horasRestantes = horasTotal - horasDistribuidas;

        List<Parcela> porMaiorResto = parcelas.stream()
                .sorted(
                        Comparator.comparingLong(Parcela::resto)
                                .reversed()
                                .thenComparing(
                                        parcela -> parcela.materia.getIdMateria()
                                )
                )
                .toList();

        for (int i = 0; i < horasRestantes; i++) {
            Parcela parcela = porMaiorResto.get(i);

            int indice = parcelas.indexOf(parcela);

            parcelas.set(
                    indice,
                    new Parcela(
                            parcela.materia(),
                            parcela.horas() + 1,
                            parcela.resto()
                    )
            );
        }

        return parcelas.stream()
                .map(parcela -> new DistribuicaoMateriaDTO(
                        parcela.materia().getIdMateria(),
                        parcela.materia().getNome(),
                        parcela.materia().getDificuldade(),
                        parcela.horas()
                ))
                .sorted(
                        Comparator.comparing(
                                DistribuicaoMateriaDTO::nomeMateria,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .toList();
    }
    @Transactional
    public void setMetaMateria(Long usuarioId, Long materiaId, Integer mes, Integer ano, Integer horasMeta) {
        validarUsuarioId(usuarioId);

        if (!materiaRepository.existsById(materiaId)) {
            throw new EntityNotFoundException("Matéria não encontrada");
        }

        if (horasMeta == null || horasMeta <= 0) {
            throw new IllegalArgumentException("As horas de meta devem ser maiores que zero");
        }

        List<Desempenho> existentes = desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(
                materiaId, mes, ano
        );

        if (!existentes.isEmpty()) {
            Desempenho desempenho = existentes.get(0);
            desempenho.setHorasMeta(horasMeta);
            desempenhoRepository.save(desempenho);
        } else {
            Desempenho novo = new Desempenho();
            novo.setUsuario(usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado")));
            novo.setMateria(materiaRepository.findById(materiaId)
                    .orElseThrow(() -> new EntityNotFoundException("Matéria não encontrada")));
            novo.setMes(mes);
            novo.setAno(ano);
            novo.setHorasConcluidas(0);
            novo.setHorasMeta(horasMeta);
            desempenhoRepository.save(novo);
        }
    }

    public Integer getMetaSemanal(Long usuarioId) {
        validarUsuarioId(usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return usuario.getMetaSemanalHoras();
    }

    public List<DistribuicaoMateriaDTO> getDistribuicaoPorMateria(Long usuarioId, Integer mes, Integer ano) {
        validarUsuarioId(usuarioId);

        List<Materia> materias = materiaRepository.findAllByUsuario_IdUsuarioAndAtivaTrue(usuarioId);

        return materias.stream()
                .map(materia -> {
                    List<Desempenho> registros = desempenhoRepository.findByMateria_IdMateriaAndMesAndAno(
                            materia.getIdMateria(), mes, ano
                    );

                    Integer horasMeta = registros.stream()
                            .mapToInt(Desempenho::getHorasMeta)
                            .sum();

                    return new DistribuicaoMateriaDTO(
                            materia.getIdMateria(),
                            materia.getNome(),
                            materia.getDificuldade(),
                            horasMeta
                    );
                })
                .collect(Collectors.toList());
    }

    public Double calcularDesempenhoPercentual(Long usuarioId) {
        validarUsuarioId(usuarioId);

        return progressoService.calcularProgressoGeral(usuarioId);
    }

    private void validarUsuarioId(Long usuarioId) {
        Objects.requireNonNull(
                usuarioId,
                "O ID do usuário deve ser positivo."
        );

        if (usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "O ID do usuário deve ser positivo."
            );
        }
    }

    private void validarMeta(Integer horasTotal) {
        Objects.requireNonNull(
                horasTotal,
                "A meta semanal não pode ser nula."
        );

        if (horasTotal <= META_MINIMA || horasTotal > META_MAXIMA) {
            throw new IllegalArgumentException(
                    "A meta semanal deve estar entre 1 e 168 horas."
            );
        }
    }

    public void validarDificuldade(Materia materia) {
        Integer dificuldade = materia.getDificuldade();

        if (dificuldade == null || dificuldade < 1 || dificuldade > 5) {
            throw new IllegalStateException(
                    "A matéria \"" + materia.getNome() + "\" deve possuir dificuldade entre 1 e 5"
            );
        }
    }

    private record Parcela(
            Materia materia,
            int horas,
            Long resto
    ) {}

}
